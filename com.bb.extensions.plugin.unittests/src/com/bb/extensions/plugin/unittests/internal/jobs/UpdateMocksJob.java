/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bb.extensions.plugin.unittests.internal.navigator.MocksUnitTestsNavigatorElement;
import com.bb.extensions.plugin.unittests.internal.nls.Messages;

/**
 * @author tallen
 * 
 */
public class UpdateMocksJob extends Job {

	/**
	 * The mocks navigator element
	 */
	private MocksUnitTestsNavigatorElement mocksNavigatorElement;

	/**
	 * @param element
	 *            The mocks navigator element
	 */
	public UpdateMocksJob(MocksUnitTestsNavigatorElement element) {
		super(Messages.UpdateMocksJob_jobTitle);
		this.mocksNavigatorElement = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (this.mocksNavigatorElement != null) {
			this.mocksNavigatorElement.updateMocks();
		}
		return Status.OK_STATUS;
	}
}
