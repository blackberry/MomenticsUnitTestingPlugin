/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.preferences;

import java.io.File;
import java.util.List;

import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.internal.core.envvar.EnvironmentVariableManager;
import org.eclipse.cdt.internal.core.envvar.UserDefinedEnvironmentSupplier;
import org.eclipse.cdt.utils.envvar.StorableEnvironment;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.utilities.FileUtils;
import com.google.common.collect.Lists;

/**
 * Class used to initialize default preference values.
 */
@SuppressWarnings("restriction")
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * Add an environment variable in vars only if it isn't already there.
	 * 
	 * @param vars
	 *            The Storable Environment variables list
	 * @param key
	 *            The key of the environment variable
	 * @param value
	 *            The new value
	 */
	private void addEnvVar(StorableEnvironment vars, String key, String value) {
		if (vars.getVariable(key) == null) {
			vars.createVariable(key, value, IEnvironmentVariable.ENVVAR_APPEND,
					";"); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_CMAKE, findCMakeExecutable());
		store.setDefault(PreferenceConstants.P_QMAKE, findQMakeExecutable());
		store.setDefault(PreferenceConstants.P_MINGW, findMinGWPath());
		store.setDefault(PreferenceConstants.P_MSYS, findMsysPath());

		UserDefinedEnvironmentSupplier fUserSupplier = EnvironmentVariableManager.fUserSupplier;
		StorableEnvironment vars = fUserSupplier.getWorkspaceEnvironmentCopy();

		addEnvVar(vars, "CMAKE_EXE", //$NON-NLS-1$
				store.getString(PreferenceConstants.P_CMAKE));
		addEnvVar(vars,
				"QMAKE_PATH", //$NON-NLS-1$
				new File(store.getString(PreferenceConstants.P_QMAKE))
						.getParent());
		addEnvVar(vars, "MINGW_PATH", //$NON-NLS-1$
				store.getString(PreferenceConstants.P_MINGW));
		addEnvVar(vars, "MSYS_PATH", //$NON-NLS-1$
				store.getString(PreferenceConstants.P_MSYS));
		fUserSupplier.setWorkspaceEnvironment(vars);
	}

	/**
	 * @return Find the CMake executable. For now it is hardcoded paths
	 */
	private String findCMakeExecutable() {
		List<String> paths = Lists.newArrayList(
				"C:\\Program Files\\CMake 2.8\\bin\\cmake.exe", //$NON-NLS-1$
				"C:\\Program Files (x86)\\CMake 2.8\\bin\\cmake.exe", //$NON-NLS-1$
				"/usr/bin/cmake"); //$NON-NLS-1$
		String result = findFile(paths);
		if (!result.isEmpty()) {
			return result;
		}
		// for linux, macosx, etc it is probably on the path
		return "cmake"; //$NON-NLS-1$
	}

	/**
	 * Search through a list of paths from top to bottom returning the first
	 * file/directory that exists
	 * 
	 * @param paths
	 *            The list of paths to search
	 * @return The first file/directory in paths that exists
	 */
	private String findFile(List<String> paths) {
		for (String path : paths) {
			if (FileUtils.exists(path)) {
				return path;
			}
		}

		return ""; //$NON-NLS-1$
	}

	/**
	 * @return Find the QMake executable. For now it is hardcoded paths
	 */
	private String findQMakeExecutable() {
		List<String> paths = Lists.newArrayList(
				"C:\\Qt\\4.8.4\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\Qt\\4.8.3\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\Qt\\4.8.2\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\Qt\\4.8.1\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\Qt\\4.8.0\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\QtSDK\\Desktop\\Qt\\4.8.4\\mingw\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\QtSDK\\Desktop\\Qt\\4.8.3\\mingw\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\QtSDK\\Desktop\\Qt\\4.8.2\\mingw\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\QtSDK\\Desktop\\Qt\\4.8.1\\mingw\\bin\\qmake.exe", //$NON-NLS-1$
				"C:\\QtSDK\\Desktop\\Qt\\4.8.0\\mingw\\bin\\qmake.exe", //$NON-NLS-1$
				"/usr/local/Trolltech/Qt-4.8.4/bin/qmake", //$NON-NLS-1$
				"/usr/local/Trolltech/Qt-4.8.3/bin/qmake", //$NON-NLS-1$
				"/usr/local/Trolltech/Qt-4.8.2/bin/qmake", //$NON-NLS-1$
				"/usr/local/Trolltech/Qt-4.8.1/bin/qmake", //$NON-NLS-1$
				"/usr/local/Trolltech/Qt-4.8.0/bin/qmake"); //$NON-NLS-1$
		String result = findFile(paths);
		if (!result.isEmpty()) {
			return result;
		}
		// for linux, macosx, etc it is probably on the path
		return "qmake"; //$NON-NLS-1$
	}

	/**
	 * @return Find the path to the mingw installation
	 */
	private String findMinGWPath() {
		List<String> paths = Lists.newArrayList("C:\\mingw\\"); //$NON-NLS-1$
		return findFile(paths);
	}

	/**
	 * @return Find the path to the msys installation
	 */
	private String findMsysPath() {
		List<String> paths = Lists.newArrayList(
				"C:\\msys\\1.0\\", "C:\\mingw\\msys\\1.0\\"); //$NON-NLS-1$
		return findFile(paths);
	}

}
