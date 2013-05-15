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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import com.bb.extensions.plugin.unittests.internal.nls.Messages;

/**
 * @author tallen
 * 
 */
public class TopLevelUnitTestsNavigatorElement extends
		BaseUnitTestsNavigatorElement {

	/**
	 * The project this belongs to
	 */
	private IProject _project;

	/**
	 * The provider of the elements
	 */
	private UnitTestsContentProvider _provider;

	/**
	 * The Tests element
	 */
	private TestsUnitTestsNavigatorElement _testsElement = null;

	/**
	 * The Support Files element
	 */
	private SupportFilesUnitTestsNavigatorElement _supportFilesElement = null;

	/**
	 * The Code Coverage element
	 */
	private CodeCoverageUnitTestsNavigatorElement _codeCoverageElement = null;

	/**
	 * The Mocks navigator element
	 */
	private MocksUnitTestsNavigatorElement _mocksElement = null;

	/**
	 * Basic constructor. The project becomes the parent of this element
	 * 
	 * @param project
	 *            The project this element will attach itself to.
	 * @param provider
	 *            The provider of the elements
	 */
	public TopLevelUnitTestsNavigatorElement(IProject project,
			UnitTestsContentProvider provider) {
		_project = project;
		_provider = provider;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getLabel()
	 */
	@Override
	public String getLabel() {
		return Messages.TopLevelUnitTestsNavigatorElement_label;
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
					"platform:/plugin/org.eclipse.cdt.ui/icons/obj16/typedef_obj.gif"); //$NON-NLS-1$
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
		return _project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getChildren()
	 */
	@Override
	public Object[] getChildren() {
		List<Object> result = new ArrayList<Object>();

		if (_testsElement == null) {
			_testsElement = new TestsUnitTestsNavigatorElement(this);
		}
		if (_supportFilesElement == null) {
			_supportFilesElement = new SupportFilesUnitTestsNavigatorElement(
					this);
		}
		if (_codeCoverageElement == null) {
			_codeCoverageElement = new CodeCoverageUnitTestsNavigatorElement(
					this);
		}
		if (_mocksElement == null) {
			_mocksElement = new MocksUnitTestsNavigatorElement(this);
		}

		result.add(_testsElement);
		result.add(_supportFilesElement);
		result.add(_codeCoverageElement);
		result.add(_mocksElement);

		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		// We always have children
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#kind()
	 */
	@Override
	public Kind kind() {
		return Kind.TopLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * BaseUnitTestsNavigatorElement#refresh()
	 */
	@Override
	public void refresh() {
		_provider.refresh(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * BaseUnitTestsNavigatorElement#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(Object element) {
		_provider.refresh(element);
	}

	/**
	 * @return The project this element belongs to
	 */
	public IProject getProject() {
		return _project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * BaseUnitTestsNavigatorElement#update(java.lang.String[])
	 */
	@Override
	public void update(String[] properties) {
		_provider.update(this, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * BaseUnitTestsNavigatorElement#update(java.lang.Object,
	 * java.lang.String[])
	 */
	@Override
	public void update(Object element, String[] properties) {
		_provider.update(element, properties);
	}
}
