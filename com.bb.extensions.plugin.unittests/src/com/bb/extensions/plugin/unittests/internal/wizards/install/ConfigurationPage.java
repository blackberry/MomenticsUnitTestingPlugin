/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.wizards.install;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.constants.Constants;
import com.bb.extensions.plugin.unittests.internal.data.DependencyInformation;
import com.bb.extensions.plugin.unittests.internal.data.DependencyInformation.Location;
import com.bb.extensions.plugin.unittests.internal.data.InstallProperties.BuildSystem;
import com.bb.extensions.plugin.unittests.internal.data.InstallProperties.TestFramework;
import com.bb.extensions.plugin.unittests.internal.preferences.PreferenceConstants;

/**
 * @author tallen
 * 
 */
public class ConfigurationPage extends WizardPage {
	/**
	 * The CMake Executable text field
	 */
	private Text _textCmakeExecutable;

	/**
	 * The QMake Executable text field
	 */
	private Text _textQmakeExecutable;

	/**
	 * The MinGW Location text field
	 */
	private Text _textMingwLocation;

	/**
	 * The MSys Location text field
	 */
	private Text _textMsysLocation;

	/**
	 * The CMake build system selection button
	 */
	private Button _btnCmake;

	/**
	 * The GTest/GMock test framework selection button
	 */
	private Button _btnGoogletestgooglemock;

	/**
	 * CMake install into project selection button
	 */
	private Button _btnCmakeInstallIntoProject;

	/**
	 * CMake use existing installation selection button
	 */
	private Button _btnCmakeUseExistingInstallation;

	/**
	 * QMake install into project selection button
	 */
	private Button _btnQmakeInstallIntoProject;

	/**
	 * QMake use existing installation selection button
	 */
	private Button _btnQmakeUseExistingInstallation;

	/**
	 * MinGW/MSys install into project selection button
	 */
	private Button _btnMingwMsysInstallIntoProject;

	/**
	 * MinGW/MSys use existing installation selection button
	 */
	private Button _btnMingwMsysUseExistingInstallation;

	/**
	 * Create the wizard.
	 */
	public ConfigurationPage() {
		super("wizardPage");
		setTitle("Install Unit Tests");
		setDescription("Configure Unit Test Installation");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.verticalSpacing = 0;
		gl_container.horizontalSpacing = 0;
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		container.setLayout(gl_container);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(
				container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandVertical(true);

		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
						SWT.DEFAULT));
			}
		});
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);

		Group grpBuildSystem = new Group(composite, SWT.NONE);
		grpBuildSystem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpBuildSystem.setText("Build System");
		grpBuildSystem.setLayout(new GridLayout(1, false));

		_btnCmake = new Button(grpBuildSystem, SWT.RADIO);
		_btnCmake.setSelection(true);
		_btnCmake.setText("CMake");

		Group grpTestFramework = new Group(composite, SWT.NONE);
		grpTestFramework.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		grpTestFramework.setLayout(new GridLayout(1, false));
		grpTestFramework.setText("Test Framework");

		_btnGoogletestgooglemock = new Button(grpTestFramework, SWT.RADIO);
		_btnGoogletestgooglemock.setSelection(true);
		_btnGoogletestgooglemock.setText("GoogleTest/GoogleMock");

		Bundle depsBundle = Platform.getBundle(Constants.DEPENDENCIES_PLUGIN);
		if (depsBundle == null) {
			Label lblTheDependenciesBelow = new Label(composite, SWT.WRAP);
			lblTheDependenciesBelow.setForeground(SWTResourceManager
					.getColor(SWT.COLOR_RED));
			GridData gd_lblTheDependenciesBelow = new GridData(SWT.FILL,
					SWT.CENTER, false, false, 1, 1);
			gd_lblTheDependenciesBelow.widthHint = 0;
			lblTheDependenciesBelow.setLayoutData(gd_lblTheDependenciesBelow);
			lblTheDependenciesBelow
					.setText("The dependencies below require the plugin com.bb.extensions.unittests.dependencies to be installed");
		}

		Group grpCmakeExecutable = new Group(composite, SWT.NONE);
		grpCmakeExecutable.setLayout(new GridLayout(2, false));
		grpCmakeExecutable.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
				false, 1, 1));
		grpCmakeExecutable.setSize(562, 87);
		grpCmakeExecutable.setText("CMake Executable");

		_btnCmakeInstallIntoProject = new Button(grpCmakeExecutable, SWT.RADIO);
		_btnCmakeInstallIntoProject.setText("Install Into Project");

		_btnCmakeUseExistingInstallation = new Button(grpCmakeExecutable,
				SWT.RADIO);
		_btnCmakeUseExistingInstallation.setSelection(true);
		_btnCmakeUseExistingInstallation.setText("Use Existing Installation");

		final Composite composite_1 = new Composite(grpCmakeExecutable,
				SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));

		_textCmakeExecutable = new Text(composite_1, SWT.BORDER);
		_textCmakeExecutable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		final Button btnCmakeExecutableBrowse = new Button(composite_1,
				SWT.NONE);
		btnCmakeExecutableBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = getFileName();
				if (s != null) {
					_textCmakeExecutable.setText(s);
				}
			}
		});
		btnCmakeExecutableBrowse.setText("Browse");

		_btnCmakeUseExistingInstallation
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean enable = _btnCmakeUseExistingInstallation
								.getSelection();
						_textCmakeExecutable.setEnabled(enable);
						btnCmakeExecutableBrowse.setEnabled(enable);
					}
				});

		Group grpQmakeExecutable = new Group(composite, SWT.NONE);
		grpQmakeExecutable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		grpQmakeExecutable.setText("QMake Executable");
		grpQmakeExecutable.setLayout(new GridLayout(2, false));

		_btnQmakeInstallIntoProject = new Button(grpQmakeExecutable, SWT.RADIO);
		_btnQmakeInstallIntoProject.setText("Install Into Project");

		_btnQmakeUseExistingInstallation = new Button(grpQmakeExecutable,
				SWT.RADIO);
		_btnQmakeUseExistingInstallation.setText("Use Existing Installation");
		_btnQmakeUseExistingInstallation.setSelection(true);

		Composite composite_2 = new Composite(grpQmakeExecutable, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		composite_2.setLayout(new GridLayout(2, false));

		_textQmakeExecutable = new Text(composite_2, SWT.BORDER);
		_textQmakeExecutable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		final Button btnQmakeExecutableBrowse = new Button(composite_2,
				SWT.NONE);
		btnQmakeExecutableBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = getFileName();
				if (s != null) {
					_textQmakeExecutable.setText(s);
				}
			}
		});
		btnQmakeExecutableBrowse.setText("Browse");

		_btnQmakeUseExistingInstallation
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean enable = _btnQmakeUseExistingInstallation
								.getSelection();
						_textQmakeExecutable.setEnabled(enable);
						btnQmakeExecutableBrowse.setEnabled(enable);
					}
				});

		Group grpMingwmsysLocations = new Group(composite, SWT.NONE);
		grpMingwmsysLocations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		grpMingwmsysLocations.setText("MinGW/MSys Locations");
		grpMingwmsysLocations.setLayout(new GridLayout(2, false));

		_btnMingwMsysInstallIntoProject = new Button(grpMingwmsysLocations,
				SWT.RADIO);
		_btnMingwMsysInstallIntoProject.setText("Install Into Project");

		_btnMingwMsysUseExistingInstallation = new Button(
				grpMingwmsysLocations, SWT.RADIO);
		_btnMingwMsysUseExistingInstallation
				.setText("Use Existing Installation");
		_btnMingwMsysUseExistingInstallation.setSelection(true);

		Composite composite_3 = new Composite(grpMingwmsysLocations, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		composite_3.setLayout(new GridLayout(3, false));

		Label lblMingw = new Label(composite_3, SWT.NONE);
		lblMingw.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMingw.setText("MinGW");

		_textMingwLocation = new Text(composite_3, SWT.BORDER);
		_textMingwLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		final Button btnMingwLocationBrowse = new Button(composite_3, SWT.NONE);
		btnMingwLocationBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String s = getLocation();
				if (s != null) {
					_textMingwLocation.setText(s);
				}
			}
		});
		btnMingwLocationBrowse.setText("Browse");

		Label lblMsys = new Label(composite_3, SWT.NONE);
		lblMsys.setText("MSys");

		_textMsysLocation = new Text(composite_3, SWT.BORDER);
		_textMsysLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));

		final Button btnMsysLocationBrowse = new Button(composite_3, SWT.NONE);
		btnMsysLocationBrowse.setText("Browse");

		_btnMingwMsysUseExistingInstallation
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean enable = _btnMingwMsysUseExistingInstallation
								.getSelection();
						_textMingwLocation.setEnabled(enable);
						btnMingwLocationBrowse.setEnabled(enable);
						_textMsysLocation.setEnabled(enable);
						btnMsysLocationBrowse.setEnabled(enable);
					}
				});

		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		_textCmakeExecutable.setText(prefs
				.getString(PreferenceConstants.P_CMAKE));
		_textQmakeExecutable.setText(prefs
				.getString(PreferenceConstants.P_QMAKE));
		_textMingwLocation
				.setText(prefs.getString(PreferenceConstants.P_MINGW));
		_textMsysLocation.setText(prefs.getString(PreferenceConstants.P_MSYS));

		if (depsBundle == null) {
			// disable all dependency controls since the dependency plugin is
			// not installed
			_btnCmakeInstallIntoProject.setEnabled(false);
			_btnCmakeUseExistingInstallation.setEnabled(false);
			btnCmakeExecutableBrowse.setEnabled(false);
			_textCmakeExecutable.setEnabled(false);
			_btnQmakeInstallIntoProject.setEnabled(false);
			_btnQmakeUseExistingInstallation.setEnabled(false);
			btnQmakeExecutableBrowse.setEnabled(false);
			_textQmakeExecutable.setEnabled(false);
			_btnMingwMsysInstallIntoProject.setEnabled(false);
			_btnMingwMsysUseExistingInstallation.setEnabled(false);
			btnMingwLocationBrowse.setEnabled(false);
			btnMsysLocationBrowse.setEnabled(false);
			_textMingwLocation.setEnabled(false);
			_textMsysLocation.setEnabled(false);
		}

		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		composite.layout();
	}

	/**
	 * Open a file dialog to select a file on the file system
	 * 
	 * @return a string describing the absolute path of the selected file, or
	 *         null if the dialog was cancelled or an error occurred
	 */
	private String getFileName() {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		if (SWT.getPlatform().equals("win32")) {
			dialog.setFilterExtensions(new String[] { "*.exe" });
		} else {
			dialog.setFilterExtensions(new String[] { "*" });
		}
		dialog.setFilterNames(new String[] { "Executable Files" });
		return dialog.open();
	}

	/**
	 * @return a string describing the absolute path of the selected directory,
	 *         or null if the dialog was cancelled or an error occurred
	 */
	private String getLocation() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		return dialog.open();
	}

	/**
	 * @return the BuildSystem
	 */
	public BuildSystem getBuildSystem() {
		return BuildSystem.CMAKE;
	}

	/**
	 * @return the TestFramework
	 */
	public TestFramework getTestFramework() {
		return TestFramework.GTEST_GMOCK;
	}

	/**
	 * @return The DependencyInformation for CMake
	 */
	public DependencyInformation getCmakeDependencyInformation() {
		Location location;
		if (_btnCmakeInstallIntoProject.getSelection()) {
			location = Location.PROJECT;
		} else {
			location = Location.LOCAL;
		}
		return new DependencyInformation(location,
				_textCmakeExecutable.getText());
	}

	/**
	 * @return The DependencyInformation for QMake
	 */
	public DependencyInformation getQmakeDependencyInformation() {
		Location location;
		if (_btnQmakeInstallIntoProject.getSelection()) {
			location = Location.PROJECT;
		} else {
			location = Location.LOCAL;
		}
		return new DependencyInformation(location,
				_textQmakeExecutable.getText());
	}

	/**
	 * @return The DependencyInformation for MinGW
	 */
	public DependencyInformation getMingwDependencyInformation() {
		Location location;
		if (_btnMingwMsysInstallIntoProject.getSelection()) {
			location = Location.PROJECT;
		} else {
			location = Location.LOCAL;
		}
		return new DependencyInformation(location, _textMingwLocation.getText());
	}

	/**
	 * @return The DependencyInformation for MSys
	 */
	public DependencyInformation getMsysDependencyInformation() {
		Location location;
		if (_btnMingwMsysInstallIntoProject.getSelection()) {
			location = Location.PROJECT;
		} else {
			location = Location.LOCAL;
		}
		return new DependencyInformation(location, _textMsysLocation.getText());
	}
}
