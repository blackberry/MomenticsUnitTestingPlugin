/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.resourceschangelistener.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author nbilal
 * 
 */
public class ResourceChangeListener implements IResourceChangeListener {

	/**
	 * 
	 */
	List<ResourceChangeListenerExtension> resourceChangeListeners = new ArrayList<ResourceChangeListenerExtension>();

	/**
	 * 
	 */
	public ResourceChangeListener() {
		// loads the elements added via the extension point
		String extensionPointId = 
				"com.bb.extensions.plugin.unittests.resourceschangelistener.resourcechangelistener";
		IConfigurationElement[] configurationsElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointId);
		for (int i = 0; i < configurationsElements.length; i++) {
			IConfigurationElement configurationElement = configurationsElements[i];
			if (configurationElement.getName().equals("ResourceChangeListener")) {
				this.resourceChangeListeners.add(new ResourceChangeListenerExtension(configurationElement));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
	 * .eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		switch (event.getType()) {
		case IResourceChangeEvent.PRE_CLOSE:
			this.notifyListeners(event, "Project Close");
			break;
		case IResourceChangeEvent.PRE_DELETE:
			this.notifyListeners(event, "Project Delete");
			break;
		}
	}

	/**
	 * Notifies all the listeners of the given listenerKind.
	 * 
	 * @param event
	 * @param listenerKind
	 */
	private void notifyListeners(IResourceChangeEvent event, String listenerKind) {
		for (ResourceChangeListenerExtension extension : this.resourceChangeListeners) {
			String kind = extension.getKind();
			if (kind.equals(listenerKind)) {
				if (extension.verifyEnablement(event)) {
					IResourceChangeListener listener = extension.getResourceChangeListenerInstance();
					if (listener != null) {
						listener.resourceChanged(event);
					}
				}
			}
		}
	}
}
