/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.bb.extensions.plugin.resourceschangelistener.listeners;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author nbilal
 * 
 */
public class ResourceChangeListenerExtension {
	/**
	 * the configuration element associated with the extension
	 */
	IConfigurationElement configurationElement;
	/**
	 * the instance
	 */
	IResourceChangeListener instance = null;

	/**
	 * @param configurationElement
	 */
	ResourceChangeListenerExtension(IConfigurationElement configurationElement) {
		this.configurationElement = configurationElement;
	}

	/**
	 * If the IResourceChangeListener instance has already been created, it is
	 * returned, otherwise a new one is created and returned.
	 * 
	 * @return the IResourceChangeListener instance.
	 */
	IResourceChangeListener getResourceChangeListenerInstance() {
		if (this.instance == null) {
			try {
				this.instance = (IResourceChangeListener) configurationElement
						.createExecutableExtension("Class");
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return this.instance;
	}

	/**
	 * @return the kind of listener defined by the extension
	 */
	public String getKind() {
		return this.configurationElement.getAttribute("Kind");
	}

	/**
	 * @param event
	 * @return true if the enablement test passed, false otherwise.
	 */
	public boolean verifyEnablement(IResourceChangeEvent event) {
		IProject project = (IProject) event.getResource();
		if (project != null) {
			IConfigurationElement[] children = this.configurationElement
					.getChildren("enablement");
			if (children.length == 1) {
				try {
					Expression expression = ExpressionConverter.getDefault()
							.perform(children[0]);
					EvaluationResult result = expression
							.evaluate(new EvaluationContext(null, project));
					return (result == EvaluationResult.TRUE);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
