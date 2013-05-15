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
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import com.bb.extensions.plugin.unittests.internal.nls.Messages;
import com.google.common.collect.Lists;

/**
 * @author tallen
 * 
 */
public class SupportFilesUnitTestsNavigatorElement extends
		BaseUnitTestsNavigatorElement implements IWrapsFolder {

	/**
	 * The folder path containing support files
	 */
	private static final String SUPPORT_FILES_FOLDER = "unittests/src"; //$NON-NLS-1$

	/**
	 * The parent element
	 */
	TopLevelUnitTestsNavigatorElement _parent;

	/**
	 * The list of children
	 */
	private List<IResource> _children = Lists.newArrayList();

	/**
	 * The resource notifier
	 */
	private IResourceChangeListener _notifier;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent element
	 */
	public SupportFilesUnitTestsNavigatorElement(
			TopLevelUnitTestsNavigatorElement parent) {
		super();

		_parent = parent;

		findChildren();

		_notifier = new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				IResourceDelta folderDelta = event.getDelta().findMember(
						_parent.getProject().getFolder(SUPPORT_FILES_FOLDER)
								.getFullPath());

				if (folderDelta != null) {
					if (folderDelta.getKind() != IResourceDelta.REMOVED) {
						findChildren();
						Runnable updater = new Runnable() {

							public synchronized void run() {
								refresh();
							}
						};

						Display.getDefault().asyncExec(updater);
					}
				}
			}
		};

		ResourcesPlugin.getWorkspace().addResourceChangeListener(_notifier,
				IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Fill the _children list with the known files/folders in the support files
	 * folder
	 */
	private void findChildren() {
		try {
			synchronized (_children) {
				_children.clear();
				_children.addAll(Lists.newArrayList(_parent.getProject()
						.getFolder(SUPPORT_FILES_FOLDER).members()));
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getLabel()
	 */
	@Override
	public String getLabel() {
		return Messages.SupportFilesUnitTestsNavigatorElement_label;
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
					"platform:/plugin/org.eclipse.cdt.ui/icons/obj16/sroot_obj.gif"); //$NON-NLS-1$
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
		return _parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getChildren()
	 */
	@Override
	public Object[] getChildren() {
		synchronized (_children) {
			return _children.toArray();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		synchronized (_children) {
			return !_children.isEmpty();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#kind()
	 */
	@Override
	public Kind kind() {
		return Kind.SupportFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bb.extensions.plugin.unittests.internal.navigator.IWrapsFolder#getFolder
	 * ()
	 */
	public IFolder getFolder() {
		return _parent.getProject().getFolder(SUPPORT_FILES_FOLDER);
	}
}
