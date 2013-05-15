/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.bb.extensions.plugin.unittests.internal.jobs.UninstallUnitTestsJob;

/**
 * @author tallen
 * 
 */
public class UninstallUnitTestsHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = null;

		IWorkbenchWindow workbenchWindow = HandlerUtil
				.getActiveWorkbenchWindow(event);
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			project = (IProject) strucSelection.getFirstElement();
		}

		int style = SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL;

		MessageBox messageBox = new MessageBox(workbenchWindow.getShell(),
				style);
		messageBox
				.setMessage("The Unit Tests will be deleted from the workspace.\n "
						+ "Do you want the unit tests to be deleted from the file system as well ?");
		int rc = messageBox.open();

		if (rc == SWT.CANCEL) {
			return null;
		}

		if (project != null) {
			Job job = new UninstallUnitTestsJob(project, rc == SWT.YES);
			job.setUser(true);
			job.schedule();
		}
		return null;
	}

}
