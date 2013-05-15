/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.bb.extensions.plugin.unittests.internal.utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.internal.core.envvar.EnvironmentVariableManager;
import org.eclipse.cdt.internal.core.envvar.UserDefinedEnvironmentSupplier;
import org.eclipse.cdt.utils.envvar.StorableEnvironment;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.preference.IPreferenceStore;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.constants.Constants;
import com.bb.extensions.plugin.unittests.internal.data.DependencyInformation.Location;
import com.bb.extensions.plugin.unittests.internal.preferences.PreferenceConstants;

/**
 * @author tallen
 * 
 */
@SuppressWarnings("restriction")
public class PreferenceUtils {
	/**
	 * Call this when the unit test preferences have been updated
	 */
	public static void updatedPreferences() {
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		UserDefinedEnvironmentSupplier fUserSupplier = EnvironmentVariableManager.fUserSupplier;
		StorableEnvironment vars = fUserSupplier.getWorkspaceEnvironmentCopy();
		addOrReplaceEnvVar(vars, "CMAKE_EXE", //$NON-NLS-1$
				prefs.getString(PreferenceConstants.P_CMAKE));
		addOrReplaceEnvVar(vars,
				"QMAKE_PATH", //$NON-NLS-1$
				new File(prefs.getString(PreferenceConstants.P_QMAKE))
						.getParent());
		addOrReplaceEnvVar(vars, "MINGW_PATH", //$NON-NLS-1$
				prefs.getString(PreferenceConstants.P_MINGW));
		addOrReplaceEnvVar(vars, "MSYS_PATH", //$NON-NLS-1$
				prefs.getString(PreferenceConstants.P_MSYS));
		fUserSupplier.setWorkspaceEnvironment(vars);

		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		try {
			ILaunchConfiguration[] launchConfigurations = launchManager
					.getLaunchConfigurations(launchManager
							.getLaunchConfigurationType("org.eclipse.cdt.testsrunner.launch.testsRunner")); //$NON-NLS-1$
			for (ILaunchConfiguration launchConfiguration : launchConfigurations) {
				IProject project = ResourcesPlugin
						.getWorkspace()
						.getRoot()
						.getProject(
								launchConfiguration.getAttribute(
										"org.eclipse.cdt.launch.PROJECT_ATTR", //$NON-NLS-1$
										"")); //$NON-NLS-1$
				if (project
						.hasNature("com.bb.extensions.plugin.unittests.unittests")) { //$NON-NLS-1$

					ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy = launchConfiguration
							.getWorkingCopy();

					updateEnvironmentVariables(prefs, project,
							launchConfigurationWorkingCopy);
					launchConfigurationWorkingCopy.doSave();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param project
	 * @param launchConfigurationWorkingCopy
	 * @throws CoreException
	 */
	public static void updateEnvironmentVariables(IProject project,
			ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy)
			throws CoreException {
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		updateEnvironmentVariables(prefs, project,
				launchConfigurationWorkingCopy);
	}

	/**
	 * @param prefs
	 *            The
	 * @param project
	 * @param launchConfigurationWorkingCopy
	 * @throws CoreException
	 */
	private static void updateEnvironmentVariables(IPreferenceStore prefs,
			IProject project,
			ILaunchConfigurationWorkingCopy launchConfigurationWorkingCopy)
			throws CoreException {
		launchConfigurationWorkingCopy.setAttribute(
				"org.eclipse.debug.core.appendEnvironmentVariables", //$NON-NLS-1$
				false);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, String> map = launchConfigurationWorkingCopy.getAttribute(
				"org.eclipse.debug.core.environmentVariables", //$NON-NLS-1$
				(Map) null);
		if (map == null) {
			map = new HashMap<String, String>();
		}
		map.put("PATH", getPathEnvVar(project, prefs, launchConfigurationWorkingCopy)); //$NON-NLS-1$
		launchConfigurationWorkingCopy.setAttribute(
				"org.eclipse.debug.core.environmentVariables", map); //$NON-NLS-1$
	}

	/**
	 * Add or replace an environment variable in vars.
	 * 
	 * @param vars
	 *            The Storable Environment variables list
	 * @param key
	 *            The key of the environment variable
	 * @param value
	 *            The new value
	 */
	private static void addOrReplaceEnvVar(StorableEnvironment vars,
			String key, String value) {
		if (vars.getVariable(key) != null) {
			vars.createVariable(key, value,
					IEnvironmentVariable.ENVVAR_REPLACE, ";"); //$NON-NLS-1$
		} else {
			vars.createVariable(key, value, IEnvironmentVariable.ENVVAR_APPEND,
					";"); //$NON-NLS-1$
		}
	}

	/**
	 * @param project
	 *            The project containing the launch configuration
	 * @param store
	 *            The preference store to use
	 * @param launchConfiguration
	 *            The launch configuration we are modifying
	 * @return The path environment variable value
	 * @throws CoreException
	 */
	private static String getPathEnvVar(IProject project,
			IPreferenceStore store,
			ILaunchConfigurationWorkingCopy launchConfiguration)
			throws CoreException {
		StringBuilder result = new StringBuilder();
		String qmakeExe;
		if (Location.fromString(launchConfiguration.getAttribute(
				Constants.QMAKE_LOCATION_ID, Location.LOCAL.toString())) == Location.LOCAL) {
			qmakeExe = store.getString(PreferenceConstants.P_QMAKE);
		} else {
			qmakeExe = project.getLocation()
					.append("unittests/builddeps/Qt/bin/qmake.exe")
					.toOSString();
		}
		result.append(new File(qmakeExe).getParent().toString());

		String mingwPath;
		if (Location.fromString(launchConfiguration.getAttribute(
				Constants.MINGW_LOCATION_ID, Location.LOCAL.toString())) == Location.LOCAL) {
			mingwPath = store.getString(PreferenceConstants.P_MINGW);
		} else {
			mingwPath = project.getLocation()
					.append("unittests/builddeps/mingw").toOSString();
		}
		String msysPath;
		if (Location.fromString(launchConfiguration.getAttribute(
				Constants.MSYS_LOCATION_ID, Location.LOCAL.toString())) == Location.LOCAL) {
			msysPath = store.getString(PreferenceConstants.P_MSYS);
		} else {
			msysPath = project.getLocation()
					.append("unittests/builddeps/msys/1.0").toOSString();
		}

		result.append(File.pathSeparator);
		result.append(mingwPath);
		result.append(File.separator);
		result.append("bin"); //$NON-NLS-1$
		result.append(File.pathSeparator);
		result.append(msysPath);
		result.append(File.separator);
		result.append("bin"); //$NON-NLS-1$

		return result.toString();
	}
}
