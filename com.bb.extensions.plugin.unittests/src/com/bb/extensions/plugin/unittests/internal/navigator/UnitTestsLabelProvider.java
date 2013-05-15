/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.bb.extensions.plugin.unittests.Activator;

/**
 * @author tallen
 * 
 */
public class UnitTestsLabelProvider implements ILabelProvider {
	/**
	 * The image registry object
	 */
	private ImageRegistry images = new ImageRegistry();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.
	 * jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		images.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang
	 * .Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
	 * .jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		Image result = null;

		if (element instanceof IUnitTestsNavigatorElement) {
			Object icon = ((IUnitTestsNavigatorElement) element).getIcon();

			if (icon instanceof String) {
				// it's a registered key
				result = Activator.getDefault().getImageRegistry()
						.get((String) icon);
			} else if (icon instanceof Image) {
				result = (Image) icon;
			} else {
				ImageDescriptor descriptor = null;
				String key = null;

				if (icon instanceof URL) {
					key = icon.toString();
					descriptor = ImageDescriptor.createFromURL((URL) icon);
				} else if (icon instanceof ImageDescriptor) {
					key = icon.toString(); // URLImageDescriptors have sensible
											// toString()s
					descriptor = (ImageDescriptor) icon;
				}

				if (key != null) {
					result = images.get(key);
					if (result == null) {
						images.put(key, descriptor);
						result = images.get(key);
					}
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IUnitTestsNavigatorElement) {
			return ((IUnitTestsNavigatorElement) element).getLabel();
		}
		return new String();
	}

}
