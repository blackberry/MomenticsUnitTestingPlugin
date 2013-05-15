/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.utilities;

/**
 * @author tallen
 * 
 */
public class SystemUtils {
	/**
	 * @return true if the system is running on windows or false otherwise
	 */
	public static boolean isWindows() {
		return getOsName().startsWith("Windows"); //$NON-NLS-1$
	}

	/**
	 * @return The OS name
	 */
	public static String getOsName() {
		return System.getProperty("os.name"); //$NON-NLS-1$
	}
}
