/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.constants.Constants;
import com.bb.extensions.plugin.unittests.internal.nls.Messages;
import com.bb.extensions.plugin.unittests.internal.utilities.DialogUtils;
import com.bb.extensions.plugin.unittests.internal.utilities.PreferenceUtils;
import com.bb.extensions.plugin.unittests.internal.utilities.SystemUtils;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class UnitTestsPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		if (SystemUtils.isWindows()) {
			final Button btnInstallDependencies = new Button(
					(Composite) control, SWT.CENTER);
			btnInstallDependencies.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					installDependencies();
				}
			});
			btnInstallDependencies.setText("Install Dependencies");
			btnInstallDependencies.setLayoutData(new GridData(SWT.RIGHT,
					SWT.CENTER, false, false, 3, 1));
		}

		return control;
	}

	/**
	 * Install Dependencies
	 */
	private void installDependencies() {
		Bundle depsBundle = Platform.getBundle(Constants.DEPENDENCIES_PLUGIN);

		if (depsBundle == null) {
			DialogUtils
					.showErrorDialog(
							"ERROR",
							"Dependencies installation requires the plugin com.bb.extensions.unittests.dependencies to be installed");
			return;
		}

		DirectoryDialog dialog = new DirectoryDialog(getShell());
		String folder = dialog.open();
		if (folder != null) {
			Job job = new InstallDependenciesJob(folder);
			job.setUser(true);
			job.schedule();
		}
	}

	/**
	 * Default constructor
	 */
	public UnitTestsPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.UnitTestsPreferencePage_description);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		{
			FileFieldEditor fileFieldEditor = new FileFieldEditor(
					PreferenceConstants.P_CMAKE,
					Messages.UnitTestsPreferencePage_cmakeExecutable,
					getFieldEditorParent());
			addField(fileFieldEditor);
		}
		addField(new FileFieldEditor(PreferenceConstants.P_QMAKE,
				Messages.UnitTestsPreferencePage_qmakeExecutable,
				getFieldEditorParent()));
		if (SystemUtils.isWindows()) {
			addField(new DirectoryFieldEditor(PreferenceConstants.P_MINGW,
					Messages.UnitTestsPreferencePage_mingwFolder,
					getFieldEditorParent()));
			addField(new DirectoryFieldEditor(PreferenceConstants.P_MSYS,
					Messages.UnitTestsPreferencePage_msysFolder,
					getFieldEditorParent()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Update the preference values
	 */
	private void updateValues() {
		PreferenceUtils.updatedPreferences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		updateValues();
		return result;
	}

}