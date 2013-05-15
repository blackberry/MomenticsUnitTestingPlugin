/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

/**
 * @author tallen
 * 
 */
public abstract class BaseContentProvider implements ICommonContentProvider {
	/**
	 * An empty object array
	 */
	public static final Object[] NOTHING = new Object[0];

	/**
	 * The viewer
	 */
	private StructuredViewer _viewer;

	/**
	 * Standard initialization
	 */
	public BaseContentProvider() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return NOTHING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] result = NOTHING;
		if (parentElement instanceof IUnitTestsNavigatorElement) {
			result = ((IUnitTestsNavigatorElement) parentElement).getChildren();
		}
		if (parentElement instanceof IProject) {
			BaseUnitTestsNavigatorElement topLevel = getTopLevelElement((IProject) parentElement);
			result = new Object[] { topLevel };
		}
		return result;
	}

	/**
	 * @param project
	 *            The project reference
	 * @return The top level element
	 */
	protected abstract BaseUnitTestsNavigatorElement getTopLevelElement(
			IProject project);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		Object result = null;

		if (element instanceof IUnitTestsNavigatorElement) {
			result = ((IUnitTestsNavigatorElement) element).getParent();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		boolean result = false;

		if (element instanceof IUnitTestsNavigatorElement) {
			result = ((IUnitTestsNavigatorElement) element).hasChildren();
		} else {
			result = element instanceof IProject;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		_viewer = (StructuredViewer) viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.IMementoAware#restoreState(org.eclipse.ui.IMemento
	 * )
	 */
	@Override
	public void restoreState(IMemento aMemento) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.IMementoAware#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento aMemento) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.ICommonContentProvider#init(org.eclipse.ui.navigator
	 * .ICommonContentExtensionSite)
	 */
	@Override
	public void init(ICommonContentExtensionSite aConfig) {
	}

	/**
	 * Refresh from the top level element down
	 * 
	 * @param project
	 *            The project to refresh
	 */
	public void refresh(IProject project) {
		refresh(getTopLevelElement(project));
	}

	/**
	 * Refresh the element and all children underneath
	 * 
	 * @param element
	 *            The element to refresh
	 */
	public void refresh(Object element) {
		_viewer.refresh(element);
	}

	/**
	 * Update from the top level element down
	 * 
	 * @param project
	 *            The project to refresh
	 * @param properties
	 *            The properties to update or null for all
	 */
	public void update(IProject project, String[] properties) {
		update(getTopLevelElement(project), properties);
	}

	/**
	 * Update the element
	 * 
	 * @param element
	 *            The element to refresh
	 * @param properties
	 *            The properties to update or null for all
	 */
	public void update(Object element, String[] properties) {
		_viewer.update(element, properties);
	}
}
