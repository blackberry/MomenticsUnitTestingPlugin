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

/**
 * @author tallen
 * 
 */
public class TestClassUnitTestsNavigatorElement extends
		TestFileUnitTestsNavigatorElement implements IHasParsedClassInformation {

	/**
	 * The class information
	 */
	private ParsedClassInformation _classInformation;

	/**
	 * This class should be created inside a Job as it parses the file (if it
	 * exists) counting how many tests there are.
	 * 
	 * @param classInformation
	 *            The class information for this element
	 * @param parent
	 *            The parent for this element
	 */
	public TestClassUnitTestsNavigatorElement(
			ParsedClassInformation classInformation,
			TestsUnitTestsNavigatorElement parent) {
		super(classInformation.getTestFile(), parent);

		_classInformation = classInformation;
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
		return _classInformation.getName() + " (" + getTestCount() + " Test" //$NON-NLS-1$ //$NON-NLS-2$
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
					"platform:/plugin/org.eclipse.cdt.ui/icons/obj16/class_obj.gif"); //$NON-NLS-1$
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
		return Kind.TestClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IHasParsedClassInformation#getClassInformation()
	 */
	public ParsedClassInformation getClassInformation() {
		return _classInformation;
	}

}
