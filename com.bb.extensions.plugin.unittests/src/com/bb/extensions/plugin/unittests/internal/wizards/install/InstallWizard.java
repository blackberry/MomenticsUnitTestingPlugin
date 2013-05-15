/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.wizards.install;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.data.DependencyInformation.Location;
import com.bb.extensions.plugin.unittests.internal.data.InstallProperties;
import com.bb.extensions.plugin.unittests.internal.jobs.InstallUnitTestsJob;
import com.bb.extensions.plugin.unittests.internal.preferences.PreferenceConstants;
import com.bb.extensions.plugin.unittests.internal.utilities.PreferenceUtils;

/**
 * @author tallen
 * 
 */
public class InstallWizard extends Wizard {

	/**
	 * The project to install into
	 */
	private IProject _project;

	/**
	 * The configuration page in the wizard
	 */
	private ConfigurationPage _configurationPage = null;

	/**
	 * @param project
	 *            The project to install into
	 * 
	 */
	public InstallWizard(IProject project) {
		setWindowTitle("Install Unit Tests");
		_project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		if (_configurationPage == null) {
			_configurationPage = new ConfigurationPage();
		}
		addPage(_configurationPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		InstallProperties properties = new InstallProperties();
		properties.setBuildSystem(_configurationPage.getBuildSystem());
		properties.setTestFramework(_configurationPage.getTestFramework());
		properties.setCmakeDependency(_configurationPage
				.getCmakeDependencyInformation());
		properties.setQmakeDependency(_configurationPage
				.getQmakeDependencyInformation());
		properties.setMingwDependency(_configurationPage
				.getMingwDependencyInformation());
		properties.setMsysDependency(_configurationPage
				.getMsysDependencyInformation());

		// update the preference for the new local install
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		boolean wereAnySet = false;
		if (properties.getCmakeDependency().getLocation() == Location.LOCAL) {
			store.setValue(PreferenceConstants.P_CMAKE, properties
					.getCmakeDependency().getPath());
			wereAnySet = true;
		}
		if (properties.getQmakeDependency().getLocation() == Location.LOCAL) {
			store.setValue(PreferenceConstants.P_QMAKE, properties
					.getQmakeDependency().getPath());
			wereAnySet = true;
		}
		if (properties.getMingwDependency().getLocation() == Location.LOCAL) {
			store.setValue(PreferenceConstants.P_MINGW, properties
					.getMingwDependency().getPath());
			wereAnySet = true;
		}
		if (properties.getMsysDependency().getLocation() == Location.LOCAL) {
			store.setValue(PreferenceConstants.P_MSYS, properties
					.getMsysDependency().getPath());
			wereAnySet = true;
		}
		if (wereAnySet) {
			PreferenceUtils.updatedPreferences();
		}

		InstallUnitTestsJob job = new InstallUnitTestsJob(_project, properties);
		job.setUser(true);
		job.schedule();
		return true;
	}
}
