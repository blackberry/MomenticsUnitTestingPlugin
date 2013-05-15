/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;

/**
 * @author tallen
 * 
 */
public class ViewerSorter extends org.eclipse.jface.viewers.ViewerSorter {

	/**
	 * 
	 */
	public ViewerSorter() {
		super(Collator.getInstance());
	}

	/**
	 * @param collator
	 */
	public ViewerSorter(Collator collator) {
		super(collator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	@Override
	public int category(Object element) {
		int result = 0;

		if (element instanceof IUnitTestsNavigatorElement) {
			result = ((IUnitTestsNavigatorElement) element).kind().ordinal();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.
	 * viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int result = category(e1) - category(e2);

		if ((result == 0) && (e1 instanceof IUnitTestsNavigatorElement)
				&& (e2 instanceof IUnitTestsNavigatorElement)) {
			IUnitTestsNavigatorElement bf1 = (IUnitTestsNavigatorElement) e1;
			IUnitTestsNavigatorElement bf2 = (IUnitTestsNavigatorElement) e2;

			@SuppressWarnings("unchecked")
			Comparator<String> comp = getComparator();

			result = comp.compare(bf1.getLabel(), bf2.getLabel());
		}

		return result;
	}

}
