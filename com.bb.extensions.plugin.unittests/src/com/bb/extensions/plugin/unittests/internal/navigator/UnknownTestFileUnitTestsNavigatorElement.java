/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;

/**
 * @author tallen
 * 
 */
public class UnknownTestFileUnitTestsNavigatorElement extends
		TestFileUnitTestsNavigatorElement {

	/**
	 * Constructor
	 * 
	 * @param testFile
	 *            The test file associated with this node
	 * @param parent
	 *            The parent for this element
	 */
	public UnknownTestFileUnitTestsNavigatorElement(IFile testFile,
			TestsUnitTestsNavigatorElement parent) {
		super(testFile, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getLabel()
	 */
	@Override
	public String getLabel() {
		int testCount = getTestCount();
		return getTestFile().getName() + " (" + getTestCount() + " Test" //$NON-NLS-1$ //$NON-NLS-2$
				+ (testCount != 1 ? "s" : "") + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getIcon()
	 */
	@Override
	public Object getIcon() {
		try {
			return new URL(
					"platform:/plugin/org.eclipse.cdt.ui/icons/obj16/unknown_obj.gif"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * TestFileUnitTestsNavigatorElement#kind()
	 */
	@Override
	public Kind kind() {
		return Kind.UnknownTestFile;
	}
}
