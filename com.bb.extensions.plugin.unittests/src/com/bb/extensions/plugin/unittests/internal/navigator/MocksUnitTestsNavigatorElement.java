/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.bb.extensions.plugin.unittests.internal.git.GitWrapper;
import com.bb.extensions.plugin.unittests.internal.utilities.DialogUtils;

/**
 * @author nbilal
 * 
 */
public class MocksUnitTestsNavigatorElement extends
		BaseUnitTestsNavigatorElement {
	/**
	 * The parent element
	 */
	TopLevelUnitTestsNavigatorElement parent;

	/**
	 * @param parent
	 */
	MocksUnitTestsNavigatorElement(TopLevelUnitTestsNavigatorElement parent) {
		this.parent = parent;
	}

	@Override
	public String getLabel() {
		return "Mocks";
	}

	@Override
	public Object getIcon() {
		try {
			return new URL(
					"platform:/plugin/org.eclipse.cdt.ui/icons/obj16/typedef_obj.gif"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
		}
		return null;
	}

	@Override
	public Object getParent() {
		return parent;
	}

	@Override
	public Object[] getChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public Kind kind() {
		return Kind.MOCKS;
	}

	/**
	 * @return true if the mocks are up to date and false otherwise.
	 */
	public boolean isUpToDate() {
		GitWrapper git = getGitWrapper();
		try {
			return git.isRepoUpToDate();
		} finally {
			if (git != null) {
				git.closeRepository();
			}
		}
	}

	/**
	 * Updates the mocks by pulling the newest version from the git repository.
	 * 
	 * @return true if the mocks have been update and false otherwise.
	 */
	public boolean updateMocks() {
		final boolean success[] = { true };
		GitWrapper git = getGitWrapper();
		try {
			if (git.isRepoClean() == false) {
				final boolean pull[] = { true };
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						Shell activeShell = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell();
						pull[0] = (MessageDialog
								.openQuestion(
										activeShell,
										"Mocks Modified",
										"The mocks git repo contains local modifications, updating may cause conflicts.\n"
												+ "Do you want to continue?"));
						success[0] = false;
					}
				});
				if (pull[0] == true) {
					if (git.pullMocks() == false) {
						DialogUtils
								.showErrorDialog("Update Mocks Error",
										"The git pull operation on the mocks repository failed.");
						success[0] = false;
					}
				}
			}
			return success[0];
		} finally {
			if (git != null) {
				git.closeRepository();
			}
		}
	}

	/**
	 * @return The project where the unit tests are installed.
	 */
	public IProject getProject() {
		return this.parent.getProject();
	}

	/**
	 * @return The git wrapper instance or null if it cannot be opened
	 */
	private GitWrapper getGitWrapper() {
		String path = this.getProject().getFolder("/unittests/bbmocks")
				.getLocation().toString();
		GitWrapper gitWrapper = GitWrapper.openRepository(path);
		return gitWrapper;
	}
}
