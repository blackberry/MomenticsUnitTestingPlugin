/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.actions;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.bb.extensions.plugin.unittests.internal.jobs.UpdateMocksJob;
import com.bb.extensions.plugin.unittests.internal.navigator.MocksUnitTestsNavigatorElement;

/**
 * @author nbilal
 * 
 */
public class UpdateMocksAction extends BaseSelectionListenerAction {

	/**
	 * The currently selected Mocks navigator element
	 */
	private MocksUnitTestsNavigatorElement selectedElement;

	/**
	 * @param text
	 */
	protected UpdateMocksAction(String text) {
		super(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (this.selectedElement != null) {
			UpdateMocksJob job = new UpdateMocksJob(this.selectedElement);
			job.setUser(true);
			job.addJobChangeListener(new IJobChangeListener() {

				@Override
				public void sleeping(IJobChangeEvent event) {
				}

				@Override
				public void scheduled(IJobChangeEvent event) {
				}

				@Override
				public void running(IJobChangeEvent event) {
				}

				@Override
				public void done(IJobChangeEvent event) {
					PlatformUI.getWorkbench().getDisplay()
							.asyncExec(new Runnable() {
								public void run() {
									selectedElement.refresh();
								}
							});
				}

				@Override
				public void awake(IJobChangeEvent event) {
				}

				@Override
				public void aboutToRun(IJobChangeEvent event) {
				}
			});
			job.schedule();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org
	 * .eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		boolean result = false;

		this.selectedElement = null;

		if (!selection.isEmpty()) {
			Object selected = selection.getFirstElement();
			if (selected instanceof MocksUnitTestsNavigatorElement) {
				this.selectedElement = (MocksUnitTestsNavigatorElement) selected;
				result = true;
			}
		}

		return result;
	}

}
