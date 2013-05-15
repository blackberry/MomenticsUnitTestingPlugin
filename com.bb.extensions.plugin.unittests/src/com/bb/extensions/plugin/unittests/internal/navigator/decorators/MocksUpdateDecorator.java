/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.navigator.MocksUnitTestsNavigatorElement;

/**
 * @author nbilal
 * 
 */
public class MocksUpdateDecorator implements ILightweightLabelDecorator {

	@Override
	public void addListener(ILabelProviderListener listener) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof MocksUnitTestsNavigatorElement) {
			MocksUnitTestsNavigatorElement mocksElement = (MocksUnitTestsNavigatorElement) element;
			if (mocksElement.isUpToDate() == false) {
				AbstractUIPlugin plugin = Activator.getDefault();
				ImageRegistry imageRegistry = plugin.getImageRegistry();
				ImageDescriptor outOfDateImageDescriptor = imageRegistry
						.getDescriptor(Activator.outOfDateImageId);

				decoration.addOverlay(outOfDateImageDescriptor,
						IDecoration.TOP_LEFT);
			}
		}
	}

}
