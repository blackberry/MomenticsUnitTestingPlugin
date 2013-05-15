/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.jobs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bb.extensions.plugin.unittests.internal.project.UnitTestFrameworkManager;

/**
 * @author nbilal
 * 
 */
public class UninstallUnitTestsJob extends Job {

	/**
	 * the project where the unit tests are currently installed
	 */
	private IProject project;
	/**
	 * indicates if the unittests should be deleted from the file system or from
	 * the workspace only.
	 */
	private boolean deleteFromFileSystem;

	/**
	 * @param project
	 */
	/**
	 * @param project
	 * @param deleteFromFileSystem
	 *            indicates if the unittests should be deleted from the file
	 *            system or from the workspace only.
	 */
	public UninstallUnitTestsJob(IProject project, boolean deleteFromFileSystem) {
		super("");
		this.project = project;
		this.deleteFromFileSystem = deleteFromFileSystem;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		UnitTestFrameworkManager manager = new UnitTestFrameworkManager(
				this.project);
		try {
			manager.uninstallUnitTests();
			manager.deleteTestFolder(this.deleteFromFileSystem);
		} catch (CoreException e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

}
