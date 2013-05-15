/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.bb.extensions.plugin.unittests.internal.constants.Constants;

/**
 * @author nbilal
 * 
 */
public class ProjectDeleteListener implements IResourceChangeListener {

	/**
	 * 
	 */
	public ProjectDeleteListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
	 * .eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResource res = event.getResource();
		if (res != null && res instanceof IProject) {
			final IProject project = (IProject) res;
			final IProject unitTestProject = ResourcesPlugin.getWorkspace()
					.getRoot().getProject(this.getUnitTestProjectName(project));
			if (unitTestProject != null && unitTestProject.exists()) {
				try {
					if (unitTestProject.hasNature(Constants.FRAMEWORK_NATURE)) {
						WorkspaceJob job = new WorkspaceJob("Deleting "
								+ unitTestProject.getName() + " project") {

							@Override
							public IStatus runInWorkspace(
									IProgressMonitor monitor)
									throws CoreException {
								unitTestProject.delete(false, true, monitor);
								return Status.OK_STATUS;
							}
						};
						job.setUser(true);
						job.schedule();
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param project
	 * @return The unit tests project name
	 */
	private String getUnitTestProjectName(IProject project) {
		return project.getName() + "unittests";
	}
}
