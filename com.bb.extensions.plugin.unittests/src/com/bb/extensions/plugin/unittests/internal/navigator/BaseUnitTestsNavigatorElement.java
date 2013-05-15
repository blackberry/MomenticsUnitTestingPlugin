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
public abstract class BaseUnitTestsNavigatorElement implements
		IUnitTestsNavigatorElement {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#refresh()
	 */
	@Override
	public void refresh() {
		refresh(this);
		Object children[] = this.getChildren();
		if (children != null) {
			for (Object o : children) {
				if (o instanceof IUnitTestsNavigatorElement) {
					((IUnitTestsNavigatorElement) o).refresh();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#refresh(java.lang.Object)
	 */
	@Override
	public void refresh(Object element) {
		if (getParent() instanceof IUnitTestsNavigatorElement) {
			((IUnitTestsNavigatorElement) getParent()).refresh(element);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#update(java.lang.String[])
	 */
	@Override
	public void update(String[] properties) {
		update(this, properties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#update(java.lang.Object, java.lang.String[])
	 */
	@Override
	public void update(Object element, String[] properties) {
		if (getParent() instanceof IUnitTestsNavigatorElement) {
			((IUnitTestsNavigatorElement) getParent()).update(element,
					properties);
		}
	}

}
