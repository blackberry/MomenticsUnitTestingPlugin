/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author tallen
 * 
 */
public class ParsedClassInformation {
	/**
	 * The class name
	 */
	private String _className;

	/**
	 * The file parsed to obtain the class
	 */
	private IFile _file;

	/**
	 * @param name
	 *            The name of the class
	 * @param file
	 *            The file parsed to obtain the class
	 */
	public ParsedClassInformation(String name, IFile file) {
		_className = name;
		_file = file;
	}

	/**
	 * @return The name of the class
	 */
	public String getName() {
		return _className;
	}

	/**
	 * @return The file
	 */
	public IFile getFile() {
		return _file;
	}

	/**
	 * @return The namespace path
	 */
	public IPath getNamespacePath() {
		IPath classPath = _file.getProjectRelativePath();
		IPath classNamespacePath = classPath.removeFirstSegments(1);
		return classNamespacePath
				.uptoSegment(classNamespacePath.segmentCount() - 1);
	}

	/**
	 * @return The test file
	 */
	public IFile getTestFile() {
		IPath classPath = _file.getProjectRelativePath();
		IPath classNamespacePath = classPath.removeFirstSegments(1);
		classNamespacePath = classNamespacePath.uptoSegment(classNamespacePath
				.segmentCount() - 1);

		IProject project = _file.getProject();

		IFile file = project.getFile("unittests/test/" //$NON-NLS-1$
				+ classNamespacePath.toString() + "/test_" + _className //$NON-NLS-1$
				+ ".cpp"); //$NON-NLS-1$
		return file;
	}
}
