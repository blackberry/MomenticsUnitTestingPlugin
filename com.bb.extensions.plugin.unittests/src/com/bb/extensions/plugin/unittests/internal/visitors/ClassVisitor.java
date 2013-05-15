/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.visitors;

import java.util.List;

import org.eclipse.cdt.core.browser.TypeUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.bb.extensions.plugin.unittests.internal.navigator.ParsedClassInformation;
import com.google.common.collect.Lists;

/**
 * @author tallen
 * 
 */
public class ClassVisitor implements ICElementVisitor {

	/**
	 * The list of parsed class names
	 */
	private List<ParsedClassInformation> _classes = Lists.newArrayList();

	/**
	 * The file being visited
	 */
	private IFile _file;

	/**
	 * @param file
	 *            The file being visited
	 * 
	 */
	public ClassVisitor(IFile file) {
		_file = file;
	}

	/**
	 * @return The list of parsed classes
	 */
	public List<ParsedClassInformation> getClasses() {
		return _classes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.core.model.ICElementVisitor#visit(org.eclipse.cdt.core
	 * .model.ICElement)
	 */
	@Override
	public boolean visit(ICElement element) throws CoreException {
		if (element.getElementType() == ICElement.C_INCLUDE) {
			// don't traverse into includes
			return false;
		}
		if (TypeUtil.isClassOrStruct(element)) {
			_classes.add(new ParsedClassInformation(new String(element
					.getElementName()), _file));
			return false;
		}
		return true;
	}
}
