/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.bb.extensions.plugin.unittests.internal.constants;

/**
 * @author nbilal
 * 
 */
public class Constants {
	/**
	 * The url of the git remote repository for blackberry cascade mocks
	 */
	public static final String BB_MOCKS_GIT_REMOTE_URL = "http://github.com/blackberry/BB10-UnitTestMocks.git";

	/**
	 * The relative path of the directory containing the mocks files in a
	 * project.
	 */
	public static final String MOCKS_PROJECT_RELATIVE_PATH = "/unittests/bbmocks";

	/**
	 * The id of the dependencies plugin
	 */
	public static final String DEPENDENCIES_PLUGIN = "com.bb.extensions.plugin.unittests.dependencies"; //$NON-NLS-1$
	/**
	 * The nature ID of the unit test framework
	 */
	public static final String UNIT_TEST_FRAMEWORK_NATURE = "com.bb.extensions.plugin.unittests.unittests"; //$NON-NLS-1$

	/**
	 * The id of the Framework nature used to filter projects
	 */
	public static final String FRAMEWORK_NATURE = "com.bb.extensions.plugin.unittests.utframework"; //$NON-NLS-1$

	/**
	 * The ID of the CMake location used in the run configuration
	 */
	public static final String CMAKE_LOCATION_ID = "com.bb.extensions.plugin.unittests.CMAKE_LOCATION"; //$NON-NLS-1$

	/**
	 * The ID of the QMake location used in the run configuration
	 */
	public static final String QMAKE_LOCATION_ID = "com.bb.extensions.plugin.unittests.QMAKE_LOCATION"; //$NON-NLS-1$

	/**
	 * The ID of the MinGW location used in the run configuration
	 */
	public static final String MINGW_LOCATION_ID = "com.bb.extensions.plugin.unittests.MINGW_LOCATION"; //$NON-NLS-1$

	/**
	 * The ID of the Msys location used in the run configuration
	 */
	public static final String MSYS_LOCATION_ID = "com.bb.extensions.plugin.unittests.MSYS_LOCATION"; //$NON-NLS-1$

}
