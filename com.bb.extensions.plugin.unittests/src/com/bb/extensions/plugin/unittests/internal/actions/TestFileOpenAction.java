/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.actions;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.part.FileEditorInput;

import com.bb.extensions.plugin.unittests.internal.navigator.IHasParsedClassInformation;
import com.bb.extensions.plugin.unittests.internal.navigator.ParsedClassInformation;
import com.bb.extensions.plugin.unittests.internal.navigator.TestFileUnitTestsNavigatorElement;
import com.bb.extensions.plugin.unittests.internal.utilities.FileUtils;

/**
 * @author tallen
 * 
 */
public class TestFileOpenAction extends BaseSelectionListenerAction {

	/**
	 * The default open action
	 */
	private final BaseSelectionListenerAction _openAction;

	/**
	 * The currently selected help item
	 */
	private TestFileUnitTestsNavigatorElement _selectedElement;

	/**
	 * The template for the test fixture to automatically insert into the test
	 * files
	 */
	private static final String _testFixtureTemplate = "\nclass {{class.name}}Test: public testing::Test {\n"
			+ "\tvirtual void SetUp() {\n"
			+ "\n"
			+ "\t}\n"
			+ "\tvirtual void TearDown() {\n" + "\t}\n" + "};\n";

	/**
	 * The sample test template
	 */
	private static final String _sampleTestTemplate = "\nTEST_F({{class.name}}Test, SampleTest) {\n"
			+ "\n}\n";

	/**
	 * @param commonOpen
	 *            The default open action
	 */
	public TestFileOpenAction(BaseSelectionListenerAction commonOpen) {
		super(commonOpen.getText());

		_openAction = commonOpen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if (_selectedElement != null) {
			IFile file = getTestFile();

			final IEditorInput editorInput = new FileEditorInput(file);
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();
			final IEditorDescriptor desc = PlatformUI.getWorkbench()
					.getEditorRegistry().getDefaultEditor(file.getName());
			Display.getDefault().asyncExec(new Runnable() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					try {
						page.openEditor(editorInput, desc.getId());
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * @return The IFile pointing to the test file
	 */
	private IFile getTestFile() {
		IFile file = _selectedElement.getTestFile();
		if (!file.exists()) {
			try {
				FileUtils.create(file);
				this.addTestTemplate(file);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * adds default includes to the test file
	 * 
	 * @param file
	 */
	private void addTestTemplate(IFile file) {
		ByteArrayOutputStream bArrayOutStream = new ByteArrayOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				bArrayOutStream));
		try {
			writer.write("#include <gmock/gmock.h>\n");

			if (_selectedElement instanceof IHasParsedClassInformation) {
				ParsedClassInformation classInformation = ((IHasParsedClassInformation) _selectedElement)
						.getClassInformation();
				IPath classNamespacePath = classInformation.getNamespacePath();

				String includeFile = "";
				if (!classNamespacePath.isEmpty()) {
					includeFile = classNamespacePath.toString() + "/"; //$NON-NLS-1$
				}
				includeFile += classInformation.getFile().getName();
				writer.write("#include <" + includeFile + ">\n");

				writer.write(_testFixtureTemplate.replace("{{class.name}}",
						classInformation.getName()));

				writer.write(_sampleTestTemplate.replace("{{class.name}}",
						classInformation.getName()));
			}

			writer.flush();
			file.setContents(
					new ByteArrayInputStream(bArrayOutStream.toByteArray()), 0,
					null);
			writer.close();
			bArrayOutStream.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
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

		_selectedElement = null;

		if (!selection.isEmpty()) {
			Object selected = selection.getFirstElement();
			if (selected instanceof TestFileUnitTestsNavigatorElement) {
				_selectedElement = (TestFileUnitTestsNavigatorElement) selected;
				result = true;
			}
		}

		_openAction.selectionChanged(selection);
		result = result || _openAction.isEnabled();

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getAccelerator()
	 */
	@Override
	public int getAccelerator() {
		return _openAction.getAccelerator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return _openAction.getDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getDisabledImageDescriptor()
	 */
	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		return _openAction.getDisabledImageDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getHoverImageDescriptor()
	 */
	@Override
	public ImageDescriptor getHoverImageDescriptor() {
		return _openAction.getHoverImageDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return _openAction.getImageDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getMenuCreator()
	 */
	@Override
	public IMenuCreator getMenuCreator() {
		return _openAction.getMenuCreator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getStyle()
	 */
	@Override
	public int getStyle() {
		return _openAction.getStyle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		return super.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return _openAction.getToolTipText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return (_selectedElement != null) || _openAction.isEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isHandled()
	 */
	@Override
	public boolean isHandled() {
		return (_selectedElement != null) || _openAction.isHandled();
	}

}
