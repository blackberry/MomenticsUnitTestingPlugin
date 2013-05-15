/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.propertyTesters;

import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author tallen
 * 
 */
public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {

	/**
	 * The list of paths to test for
	 */
	private static final List<IPath> testPaths = Arrays
			.asList(new IPath[] { new Path("unittests") }); //$NON-NLS-1$

	/**
	 * 
	 */
	public PropertyTester() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 * java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if ("isTestFramework".equals(property)) { //$NON-NLS-1$
			if (receiver instanceof IFolder) {
				IFolder folder = (IFolder) receiver;
				return (testPaths.contains(folder.getProjectRelativePath()));
			} else if (receiver instanceof ISourceRoot) {
				return testPaths.contains(((ISourceRoot) receiver).getPath());
			}
		}
		return false;
	}
}
