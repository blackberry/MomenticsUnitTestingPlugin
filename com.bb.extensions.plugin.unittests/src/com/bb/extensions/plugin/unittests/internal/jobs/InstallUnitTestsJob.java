/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.bb.extensions.plugin.unittests.internal.jobs;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bb.extensions.plugin.unittests.internal.data.InstallProperties;
import com.bb.extensions.plugin.unittests.internal.project.UnitTestFrameworkManager;

/**
 * @author nbilal
 * 
 */
public class InstallUnitTestsJob extends Job {

	/**
	 * the project where the unit tests will be installed
	 */
	private IProject project;

	/**
	 * the install properties
	 */
	private InstallProperties installProperties;

	/**
	 * @param project
	 * @param installProperties
	 */
	public InstallUnitTestsJob(IProject project,
			InstallProperties installProperties) {
		super("");
		this.project = project;
		this.installProperties = installProperties;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		UnitTestFrameworkManager manager = new UnitTestFrameworkManager(
				this.project);
		manager.setInstallProperties(this.installProperties);
		try {
			manager.installUnitTests(monitor);
		} catch (CoreException e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		} catch (IOException e) {
			e.printStackTrace();
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

}
