/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.utilities;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.bb.extensions.plugin.unittests.internal.nls.Messages;

/**
 * @author tallen
 * 
 */
public class DialogUtils {

	/**
	 * Asynchronously show an error dialog if statuses is non-empty
	 * 
	 * @param pluginId
	 *            The plugin id
	 * @param statuses
	 *            The error status messages to show
	 */
	public static void showErrorDialog(String pluginId, List<IStatus> statuses) {
		if (!statuses.isEmpty()) {
			final IStatus theStatus;
			if (statuses.size() > 1) {
				final MultiStatus info = new MultiStatus(pluginId, 1,
						Messages.DialogUtils_ErrorDescription, null);
				for (IStatus status : statuses) {
					info.add(status);
				}
				theStatus = info;
			} else {
				theStatus = statuses.get(0);
			}
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					Shell activeShell = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell();
					ErrorDialog.openError(activeShell,
							Messages.DialogUtils_ErrorTitle, null, theStatus);
				}
			});
		}
	}

	/**
	 * Synchronously show a simple error dialog
	 * 
	 * @param title
	 *            The title of the error dialog
	 * @param message
	 *            The message of the error dialog
	 */
	public static void showErrorDialog(final String title, final String message) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(workbench.getActiveWorkbenchWindow()
						.getShell(), title, message);
			}
		});
	}
}
