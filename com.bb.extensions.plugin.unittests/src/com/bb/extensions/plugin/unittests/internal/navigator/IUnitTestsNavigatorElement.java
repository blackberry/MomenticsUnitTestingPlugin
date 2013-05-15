/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

/**
 * @author tallen
 * 
 */
public interface IUnitTestsNavigatorElement {
	/**
	 * The different types of extensions navigator elements
	 */
	enum Kind {
		/**
		 * TopLevelUnitTestsNavigatorElement
		 */
		TopLevel,
		/**
		 * TestsUnitTestsNavigatorElement
		 */
		Tests,
		/**
		 * SupportFilesUnitTestsNavigatorElement
		 */
		SupportFiles,
		/**
		 * CodeCoverageUnitTestsNavigatorElement
		 */
		CodeCoverage,
		/**
		 * TestClassUnitTestsNavigatorElement
		 */
		TestClass,
		/**
		 * TestFileUnitTestsNavigatorElement
		 */
		TestFile,
		/**
		 * UnknownTestFileUnitTestsNavigatorElement
		 */
		UnknownTestFile,
		/**
		 * the mocks node
		 */
		MOCKS
	}

	/**
	 * @return The label for this element
	 */
	String getLabel();

	/**
	 * @return The icon for this element
	 */
	Object getIcon();

	/**
	 * @return The parent of this element
	 */
	Object getParent();

	/**
	 * @return An array of children below this element
	 */
	Object[] getChildren();

	/**
	 * @return True if this element has children, false otherwise
	 */
	boolean hasChildren();

	/**
	 * @return The kind of ExtensionsNavigatorElement
	 */
	Kind kind();

	/**
	 * Refresh this element
	 */
	void refresh();

	/**
	 * Refresh the given element
	 * 
	 * @param element
	 *            The element to refresh
	 */
	void refresh(Object element);

	/**
	 * Update this element
	 * 
	 * @param properties
	 *            The properties to update or null for all
	 */
	void update(String[] properties);

	/**
	 * Update the given element
	 * 
	 * @param element
	 *            The element to update
	 * @param properties
	 *            The properties to update or null for all
	 */
	void update(Object element, String[] properties);
}
