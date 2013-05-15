/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.actions;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.RenameResourceAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.navigator.TestFileUnitTestsNavigatorElement;
import com.bb.extensions.plugin.unittests.internal.navigator.UnknownTestFileUnitTestsNavigatorElement;
import com.google.common.collect.Lists;

/**
 * @author tallen
 * 
 */
public class TestFileActionProvider extends CommonActionProvider {
	/**
	 * The open action
	 */
	private BaseSelectionListenerAction _openAction;

	/**
	 * The delete action
	 */
	private BaseSelectionListenerAction _deleteAction;

	/**
	 * The rename action
	 */
	private BaseSelectionListenerAction _renameAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars
	 * )
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);

		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
				_openAction);
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				_deleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(),
				_renameAction);

		actionBars.updateActionBars();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator
	 * .ICommonActionExtensionSite)
	 */
	@Override
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);

		if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			OpenFileAction commonOpen = new OpenFileAction(
					((ICommonViewerWorkbenchSite) aSite.getViewSite())
							.getPage());
			_openAction = new TestFileOpenAction(commonOpen);
		} else {
			_openAction = new OpenFileAction(Activator.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage());
		}

		IShellProvider provider = Activator.getDefault().getWorkbench()
				.getModalDialogShellProvider();
		_deleteAction = new DeleteResourceAction(provider);

		_renameAction = new RenameResourceAction(provider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.
	 * action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, _openAction);
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, _deleteAction);
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, _renameAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.ActionGroup#setContext(org.eclipse.ui.actions.
	 * ActionContext)
	 */
	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);

		if ((context != null)
				&& (context.getSelection() instanceof IStructuredSelection)) {
			IStructuredSelection selection = (IStructuredSelection) context
					.getSelection();

			_openAction.selectionChanged(selection);

			// because we are using the default delete and rename actions we
			// need a list of IResource objects. Rename is only allowed on the
			// UnknownTestFile node
			List<IResource> renameResourceList = Lists
					.newArrayListWithExpectedSize(selection.size());
			List<IResource> deleteResourceList = Lists
					.newArrayListWithExpectedSize(selection.size());
			for (Object o : selection.toList()) {
				if (o instanceof UnknownTestFileUnitTestsNavigatorElement) {
					IFile file = ((UnknownTestFileUnitTestsNavigatorElement) o)
							.getTestFile();
					addResourceIfItExists(deleteResourceList, file);
					renameResourceList.add(file);
				} else if (o instanceof TestFileUnitTestsNavigatorElement) {
					addResourceIfItExists(deleteResourceList,
							((TestFileUnitTestsNavigatorElement) o)
									.getTestFile());
				}
			}
			StructuredSelection renameResourceSelection = new StructuredSelection(
					renameResourceList);
			StructuredSelection deleteResourceSelection = new StructuredSelection(
					deleteResourceList);
			_deleteAction.selectionChanged(deleteResourceSelection);
			_renameAction.selectionChanged(renameResourceSelection);
		}
	}

	/**
	 * Add resource to list if and only if resource exists
	 * 
	 * @param list
	 *            The list to add the resource to
	 * @param resource
	 *            The resource to add
	 */
	private void addResourceIfItExists(List<IResource> list, IResource resource) {
		if (resource.exists()) {
			list.add(resource);
		}
	}
}
