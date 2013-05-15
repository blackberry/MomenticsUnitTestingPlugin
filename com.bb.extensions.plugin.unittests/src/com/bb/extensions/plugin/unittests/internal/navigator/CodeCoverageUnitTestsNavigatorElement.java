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

import org.eclipse.core.resources.IProject;

import com.bb.extensions.plugin.unittests.internal.nls.Messages;

/**
 * @author tallen
 * 
 */
public class CodeCoverageUnitTestsNavigatorElement extends
		BaseUnitTestsNavigatorElement {

	/**
	 * The string path to the folder containing GCDA files
	 */
	public static final String GCDA_FOLDER = "unittests/build"; //$NON-NLS-1$

	/**
	 * The parent element
	 */
	TopLevelUnitTestsNavigatorElement _parent;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent element
	 */
	public CodeCoverageUnitTestsNavigatorElement(
			TopLevelUnitTestsNavigatorElement parent) {
		super();

		_parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getLabel()
	 */
	@Override
	public String getLabel() {
		return Messages.CodeCoverageUnitTestsNavigatorElement_label;
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
					"platform:/plugin/org.eclipse.linuxtools.gcov.core/icons/toggle.gif"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getParent()
	 */
	@Override
	public Object getParent() {
		return _parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getChildren()
	 */
	@Override
	public Object[] getChildren() {
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#kind()
	 */
	@Override
	public Kind kind() {
		return Kind.CodeCoverage;
	}

	/**
	 * @return The project this element belongs to
	 */
	public IProject getProject() {
		return _parent.getProject();
	}
}
