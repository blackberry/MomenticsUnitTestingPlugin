/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.wizards.filewizard.NewHeaderFileCreationWizardPage;
import org.eclipse.cdt.internal.ui.wizards.filewizard.NewSourceFileCreationWizardPage;
import org.eclipse.cdt.ui.wizards.NewHeaderFileCreationWizard;
import org.eclipse.cdt.ui.wizards.NewSourceFileCreationWizard;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.bb.extensions.plugin.unittests.internal.navigator.IWrapsFolder;
import com.bb.extensions.plugin.unittests.internal.nls.Messages;

/**
 * @author tallen
 * 
 */
@SuppressWarnings("restriction")
public class FileNewAction extends BaseSelectionListenerAction {

	/**
	 * @author tallen
	 * 
	 */
	public enum FileNewActionType {
		/**
		 * Create a new source file
		 */
		SOURCE_FILE,
		/**
		 * Create a new header file
		 */
		HEADER_FILE
	}

	/**
	 * The type of the action
	 */
	private FileNewActionType _type;

	/**
	 * @param type
	 *            The type of new action
	 */
	protected FileNewAction(FileNewActionType type) {
		super(""); //$NON-NLS-1$
		_type = type;
		switch (_type) {
		case HEADER_FILE:
			setText(Messages.FileNewAction_newHeaderFile);
			break;
		case SOURCE_FILE:
			setText(Messages.FileNewAction_newSourceFile);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		Object obj = getStructuredSelection().getFirstElement();
		if (obj instanceof IWrapsFolder) {
			IResource supportFilesFolder = ((IWrapsFolder) obj).getFolder();
			final StructuredSelection selection = new StructuredSelection(
					supportFilesFolder);

			IWorkbenchWizard wizard = null;
			switch (_type) {
			case HEADER_FILE:
				wizard = new NewHeaderFileCreationWizard();
				break;
			default:
				wizard = new NewSourceFileCreationWizard();
				break;
			}

			wizard.init(PlatformUI.getWorkbench(), selection);
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), wizard);
			dialog.setHelpAvailable(false);
			dialog.open();

			try {
				IWorkingCopy wc = null;
				for (IWizardPage page : wizard.getPages()) {
					if (page instanceof NewSourceFileCreationWizardPage) {
						wc = ((NewSourceFileCreationWizardPage) page)
								.getCreatedFileTU().getWorkingCopy();
						break;
					} else if (page instanceof NewHeaderFileCreationWizardPage) {
						wc = ((NewHeaderFileCreationWizardPage) page)
								.getCreatedFileTU().getWorkingCopy();
						break;
					}
				}

				if (wc != null) {
					Util.addInclude(wc, "gmock/gmock.h"); //$NON-NLS-1$
					wc.commit(true, new NullProgressMonitor());
				}
			} catch (CModelException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		try {
			URL imageUrl;
			switch (_type) {
			case HEADER_FILE:
				imageUrl = new URL(
						"platform:/plugin/org.eclipse.cdt.ui/icons/etool16/newhfile_wiz.gif"); //$NON-NLS-1$
				break;
			default:
				imageUrl = new URL(
						"platform:/plugin/org.eclipse.cdt.ui/icons/etool16/newcfile_wiz.gif"); //$NON-NLS-1$
				break;
			}
			return ImageDescriptor.createFromURL(imageUrl);
		} catch (MalformedURLException e) {
		}
		return null;
	}
}
