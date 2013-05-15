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
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.constants.Constants;
import com.bb.extensions.plugin.unittests.internal.utilities.PreferenceUtils;
import com.bb.extensions.plugin.unittests.internal.utilities.ZipUtils;

/**
 * @author tallen
 * 
 */
public class InstallDependenciesJob extends Job {
	/**
	 * The folder to install into
	 */
	private String _folder;

	/**
	 * @param folder
	 */
	public InstallDependenciesJob(String folder) {
		super("Install Dependencies");
		_folder = folder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Bundle depsBundle = Platform.getBundle(Constants.DEPENDENCIES_PLUGIN);
		try {
			ZipUtils.extractZip(depsBundle.getEntry("files/cmake.zip") //$NON-NLS-1$
					.openStream(), _folder);
			ZipUtils.extractZip(depsBundle.getEntry("files/qt.zip") //$NON-NLS-1$
					.openStream(), _folder);
			ZipUtils.extractZip(depsBundle.getEntry("files/mingw.zip")//$NON-NLS-1$
					.openStream(), _folder);
			ZipUtils.extractZip(depsBundle.getEntry("files/msys.zip") //$NON-NLS-1$
					.openStream(), _folder);

			IPreferenceStore store = Activator.getDefault()
					.getPreferenceStore();
			store.setValue(PreferenceConstants.P_CMAKE, _folder
					+ File.separator + "CMake" + File.separator + "bin"
					+ File.separator + "cmake.exe");
			store.setValue(PreferenceConstants.P_QMAKE, _folder
					+ File.separator + "Qt" + File.separator + "bin"
					+ File.separator + "qmake.exe");
			store.setValue(PreferenceConstants.P_MINGW, _folder
					+ File.separator + "mingw" + File.separator);
			store.setValue(PreferenceConstants.P_MSYS, _folder + File.separator
					+ "msys" + File.separator + "1.0" + File.separator);
			PreferenceUtils.updatedPreferences();
		} catch (IOException e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
}
