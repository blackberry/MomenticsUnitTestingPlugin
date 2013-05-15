/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.nls;

import org.eclipse.osgi.util.NLS;

@SuppressWarnings("javadoc")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.bb.extensions.plugin.unittests.internal.nls.messages"; //$NON-NLS-1$
	public static String CodeCoverageOpenAction_noCodeCoverageFilesError;
	public static String CodeCoverageUnitTestsNavigatorElement_label;
	public static String DialogUtils_ErrorDescription;
	public static String DialogUtils_ErrorTitle;
	public static String FileNewAction_newHeaderFile;
	public static String FileNewAction_newSourceFile;
	public static String SupportFilesUnitTestsNavigatorElement_label;
	public static String TestsUnitTestsNavigatorElement_label;
	public static String TestsUnitTestsNavigatorElement_refreshJobName;
	public static String TopLevelUnitTestsNavigatorElement_label;
	public static String UnitTestFrameworkManagerJob_installFramework;
	public static String UnitTestFrameworkManagerJob_uninstallFramework;
	public static String UnitTestsPreferencePage_cmakeExecutable;
	public static String UnitTestsPreferencePage_description;
	public static String UnitTestsPreferencePage_gitExecutable;
	public static String UnitTestsPreferencePage_mingwFolder;
	public static String UnitTestsPreferencePage_msysFolder;
	public static String UnitTestsPreferencePage_qmakeExecutable;
	public static String UpdateMocksJob_jobTitle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
