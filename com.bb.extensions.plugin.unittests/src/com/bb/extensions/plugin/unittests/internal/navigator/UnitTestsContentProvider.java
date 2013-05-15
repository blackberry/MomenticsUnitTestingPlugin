/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.util.Map;

import org.eclipse.core.resources.IProject;

import com.google.common.collect.Maps;

/**
 * @author tallen
 * 
 */
public class UnitTestsContentProvider extends BaseContentProvider {
	/**
	 * The top level element mappings
	 */
	private Map<IProject, TopLevelUnitTestsNavigatorElement> _topLevelElements = Maps
			.newHashMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bb.extensions.plugin.unittests.internal.navigator.BaseContentProvider
	 * #getTopLevelElement(org.eclipse.core.resources.IProject)
	 */
	@Override
	protected BaseUnitTestsNavigatorElement getTopLevelElement(IProject project) {
		TopLevelUnitTestsNavigatorElement topLevelElement = _topLevelElements
				.get(project);
		if (topLevelElement == null) {
			topLevelElement = new TopLevelUnitTestsNavigatorElement(project,
					this);
			_topLevelElements.put(project, topLevelElement);
		}
		return topLevelElement;
	}
}
