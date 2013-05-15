/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.commons.io.FileUtils;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.CIncludePathEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICFolderDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.make.core.IMakeBuilderInfo;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.osgi.framework.Bundle;

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.constants.Constants;
import com.bb.extensions.plugin.unittests.internal.data.DependencyInformation.Location;
import com.bb.extensions.plugin.unittests.internal.data.InstallProperties;
import com.bb.extensions.plugin.unittests.internal.git.GitWrapper;
import com.bb.extensions.plugin.unittests.internal.utilities.DialogUtils;
import com.bb.extensions.plugin.unittests.internal.utilities.PreferenceUtils;
import com.bb.extensions.plugin.unittests.internal.utilities.SystemUtils;
import com.bb.extensions.plugin.unittests.internal.utilities.ZipUtils;
import com.google.common.collect.Lists;

/**
 * @author tallen
 * 
 */
@SuppressWarnings("restriction")
public class UnitTestFrameworkManager {
	/**
	 * The name of the unit test build configuration
	 */
	private static final String UNIT_TESTS_BUILD_CONFIGURATION_NAME = "Unit Tests"; //$NON-NLS-1$

	/**
	 * The ID of the GNU Elf binary parser
	 */
	private static final String GNU_ELF_BINARY_PARSER = "org.eclipse.cdt.core.GNU_ELF"; //$NON-NLS-1$

	/**
	 * The ID of the PE binary parser
	 */
	private static final String PE_BINARY_PARSER = "org.eclipse.cdt.core.PE"; //$NON-NLS-1$

	/**
	 * The selected project
	 */
	private IProject _project;

	/**
	 * The installation properties
	 */
	private InstallProperties _installProperties = null;

	/**
	 * @param project
	 *            The selected project
	 */
	public UnitTestFrameworkManager(IProject project) {
		_project = project;
	}

	/**
	 * Uninstall the unit test framework
	 * 
	 * @throws CoreException
	 */
	public void uninstallUnitTests() throws CoreException {
		removeNature();
		removeBuildConfiguration();
	}

	/**
	 * Install the unit test framework
	 * 
	 * @param monitor
	 *            The monitor to use for progress updates
	 * @throws CoreException
	 * @throws IOException
	 */
	public void installUnitTests(IProgressMonitor monitor)
			throws CoreException, IOException {
		addNature();

		boolean isNewProjectStructure = isNewProjectStructure();

		Configuration config = createBuildConfiguration(_project);

		createTestFolder(isNewProjectStructure);

		createLaunchConfigurationXml(config);

		try {
			extractFramework();
		} catch (IOException e) {
			e.printStackTrace();
		}

		handleDependencies();

		if (isNewProjectStructure) {
			// we only create a framework project if we are the new project
			// structure. "Old" project structures have the unit tests installed
			// directly inside so they will continue to work as usual
			createFrameworkProject();
		}

		monitor.setTaskName("Cloning Mocks");
		cloneMocks();
	}

	/**
	 * Extract the dependencies into the project if required
	 * 
	 * @throws IOException
	 * @throws CoreException
	 */
	private void handleDependencies() throws IOException, CoreException {
		Bundle thisBundle = Platform.getBundle(Constants.DEPENDENCIES_PLUGIN);
		if (thisBundle != null) {
			IFolder depsFolder = _project.getFolder("unittests/builddeps"); //$NON-NLS-1$
			if (_installProperties.getCmakeDependency().getLocation() == Location.PROJECT) {
				extractZip(thisBundle.getEntry("files/cmake.zip"), depsFolder); //$NON-NLS-1$
			}
			if (_installProperties.getQmakeDependency().getLocation() == Location.PROJECT) {
				extractZip(thisBundle.getEntry("files/qt.zip"), depsFolder); //$NON-NLS-1$
			}
			if (_installProperties.getMingwDependency().getLocation() == Location.PROJECT) {
				extractZip(thisBundle.getEntry("files/mingw.zip"), depsFolder); //$NON-NLS-1$
			}
			if (_installProperties.getMsysDependency().getLocation() == Location.PROJECT) {
				extractZip(thisBundle.getEntry("files/msys.zip"), depsFolder); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Extract a zip file to destination. If destination does not exist it will
	 * attempt to be created
	 * 
	 * @param zipFile
	 *            The URL of the zip file to extract
	 * @param destination
	 *            The destination folder. If it does not exist it will attempt
	 *            to be created.
	 * @throws IOException
	 * @throws CoreException
	 */
	private void extractZip(URL zipFile, IFolder destination)
			throws IOException, CoreException {
		ZipUtils.extractZip(zipFile.openStream(), destination); //$NON-NLS-1$
	}

	/**
	 * Create the framework project
	 */
	private void createFrameworkProject() {
		IPath utPath = _project.getFolder("unittests").getLocation()
				.addTrailingSeparator();

		IProjectDescription newDesc = ResourcesPlugin.getWorkspace()
				.newProjectDescription(this.getUnitTestProjectName());
		newDesc.setLocation(utPath);

		String[] natures = newDesc.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[newNatures.length - 1] = Constants.FRAMEWORK_NATURE;
		newDesc.setNatureIds(newNatures);

		IProject projectHandle = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(newDesc.getName());
		try {
			IProject project = CCorePlugin.getDefault().createCDTProject(
					newDesc, projectHandle, new NullProgressMonitor());

			createBuildConfiguration(project);
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the project is using the new recursive make structure
	 * 
	 * @return true if and only if the project is using the new recursive make
	 *         structure with sub folders
	 */
	private boolean isNewProjectStructure() {
		// We have to do some educated guessing here.
		File projectParentFolder = _project.getLocation().toFile()
				.getParentFile();
		if ((projectParentFolder == null) || (!projectParentFolder.exists())) {
			return false;
		}
		File buildFolder = new File(projectParentFolder, "build");
		File parentMakefile = new File(projectParentFolder, "Makefile");
		IFolder projectNtoFolder = _project.getFolder("nto");
		return buildFolder.exists() && buildFolder.isDirectory()
				&& parentMakefile.exists() && parentMakefile.isFile()
				&& projectNtoFolder.exists();
	}

	/**
	 * Create the test folder if it does not already exist in the project
	 * 
	 * @param isNewProjectStructure
	 * 
	 * @throws CoreException
	 */
	private void createTestFolder(boolean isNewProjectStructure)
			throws CoreException {
		IFolder testFolder = _project.getFolder("unittests"); //$NON-NLS-1$

		if (isNewProjectStructure) {
			// TODO: if the unittests folder is already there, ask the user as
			// we are going to delete it!
			if (testFolder.exists()) {
				testFolder.delete(true, new NullProgressMonitor());
			}
			File projectParentFolder = _project.getLocation().toFile()
					.getParentFile();
			File realTestFolder = new File(projectParentFolder, "unittests");
			if (realTestFolder.exists() == false) {
				realTestFolder.mkdir();
			}
			testFolder.createLink(new Path("PARENT-1-PROJECT_LOC/unittests"),
					IResource.NONE, new NullProgressMonitor());
		} else {
			if (testFolder.exists() == false) {
				testFolder.create(true, true, new NullProgressMonitor());
			}
		}
		createProjectPathsFile(isNewProjectStructure);
	}

	/**
	 * Create the project_paths.cmake file
	 * 
	 * @param isNewProjectStructure
	 *            Whether or not the project follows the new QNX recursive make
	 * @throws CoreException
	 */
	private void createProjectPathsFile(boolean isNewProjectStructure)
			throws CoreException {
		IFile launchFile = _project.getFile("unittests/project_paths.cmake"); //$NON-NLS-1$
		String subPath;
		if (isNewProjectStructure) {
			subPath = _project.getLocation().lastSegment();
		} else {
			subPath = "";
		}
		String contents = "set( ENV{TARGET_PROJECT_LOCATION} ../" + subPath
				+ " )";
		InputStream source = new ByteArrayInputStream(contents.getBytes());
		if (launchFile.exists()) {
			launchFile.setContents(source, 0, new NullProgressMonitor());
		} else {
			launchFile.create(source, true, new NullProgressMonitor());
		}
	}

	/**
	 * Delete the test folder if it exists. This also implicitly removes the two
	 * test source folders from the project and the launch configuration
	 * 
	 * @param deleteFromFileSystem
	 *            If true, the whole unit tests folder will be deleted from the
	 *            file system. If false, only the unit tests project will be
	 *            deleted from the workspace, all source files are kept in the
	 *            file system.
	 * 
	 * @throws CoreException
	 */
	public void deleteTestFolder(boolean deleteFromFileSystem)
			throws CoreException {
		String unitTestsProjectName = this.getUnitTestProjectName();
		IProject unitTestProject = _project.getWorkspace().getRoot()
				.getProject(unitTestsProjectName);
		if (unitTestProject != null && unitTestProject.exists()) {
			if (unitTestProject.hasNature(Constants.FRAMEWORK_NATURE)) {
				unitTestProject.delete(deleteFromFileSystem, true, null);
			}
		}

		IFolder testFolder = _project.getFolder("unittests"); //$NON-NLS-1$
		boolean isLinked = testFolder.isLinked();

		if (testFolder.exists() && (isLinked || deleteFromFileSystem)) {
			testFolder.delete(true, true, null);
		}
		if (isLinked && deleteFromFileSystem) {
			// this was a linked folder. We need to remove the actual folder
			IPath projectPath = _project.getLocation();
			File realTestFolder = new File(
					projectPath.toFile().getParentFile(), "unittests");
			if (realTestFolder.exists()) {
				try {
					FileUtils.deleteDirectory(realTestFolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return The unit tests project name
	 */
	private String getUnitTestProjectName() {
		return _project.getName() + "unittests";
	}

	/**
	 * Create the unit tests launch configuration file in the test folder
	 * 
	 * @param config
	 *            The Unit Tests build configuration
	 * @throws CoreException
	 * @throws InvalidActivityException
	 */
	private void createLaunchConfigurationXml(Configuration config)
			throws CoreException, InvalidActivityException {
		IFolder unittestsDir = _project.getFolder("unittests"); //$NON-NLS-1$
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType configType = getTestsRunnerLaunchConfigurationType();
		ILaunchConfigurationWorkingCopy launchConfiguration = configType
				.newInstance(unittestsDir, _project.getName() + " Unit Tests"); //$NON-NLS-1$
		launchConfiguration.setAttribute(
				"org.eclipse.cdt.launch.ATTR_BUILD_BEFORE_LAUNCH_ATTR", 2);
		launchConfiguration.setAttribute(
				"org.eclipse.cdt.launch.COREFILE_PATH", "");
		String exeName = "unittests/build/unittests";
		if (SystemUtils.isWindows()) {
			exeName += ".exe";
		}
		launchConfiguration.setAttribute("org.eclipse.cdt.launch.PROGRAM_NAME",
				exeName);
		launchConfiguration.setAttribute("org.eclipse.cdt.launch.PROJECT_ATTR",
				_project.getName());
		launchConfiguration.setAttribute(
				"org.eclipse.cdt.launch.PROJECT_BUILD_CONFIG_AUTO_ATTR", false);
		launchConfiguration.setAttribute(
				"org.eclipse.cdt.launch.PROJECT_BUILD_CONFIG_ID_ATTR",
				config.getId());
		// for momentics based off indigo
		launchConfiguration.setAttribute("org.eclipse.cdt.launch.TESTS_RUNNER",
				"org.eclipse.cdt.testsrunner.gtest");
		// for momentics based off juno
		launchConfiguration.setAttribute(
				"org.eclipse.cdt.testsrunner.launch.TESTS_RUNNER",
				"org.eclipse.cdt.testsrunner.gtest");
		launchConfiguration.setAttribute(
				"com.bb.extensions.plugin.unittests.QMAKE_LOCATION",
				_installProperties.getQmakeDependency().getLocation()
						.toString());
		launchConfiguration.setAttribute(
				"com.bb.extensions.plugin.unittests.MINGW_LOCATION",
				_installProperties.getMingwDependency().getLocation()
						.toString());
		launchConfiguration
				.setAttribute(
						"com.bb.extensions.plugin.unittests.MSYS_LOCATION",
						_installProperties.getMsysDependency().getLocation()
								.toString());
		List<String> resourcePaths = Lists.newArrayListWithCapacity(1);
		resourcePaths.add(_project.getFullPath().toString());
		launchConfiguration.setAttribute(
				"org.eclipse.debug.core.MAPPED_RESOURCE_PATHS", resourcePaths);
		List<String> resourceTypes = Lists.newArrayListWithCapacity(1);
		resourceTypes.add("4");
		launchConfiguration.setAttribute(
				"org.eclipse.debug.core.MAPPED_RESOURCE_TYPES", resourceTypes);
		PreferenceUtils.updateEnvironmentVariables(_project,
				launchConfiguration);
		launchConfiguration.doSave();
	}

	/**
	 * @return The launch configuration type for the CDT Tests Runner
	 * @throws InvalidActivityException
	 */
	private ILaunchConfigurationType getTestsRunnerLaunchConfigurationType()
			throws InvalidActivityException {
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		for (ILaunchConfigurationType configType : launchManager
				.getLaunchConfigurationTypes()) {
			if (configType.getIdentifier().toLowerCase()
					.endsWith("testsrunner")) {
				return configType;
			}
		}
		throw new InvalidActivityException("No Tests Runner Installed");
	}

	/**
	 * Create the Unit Tests build configuration
	 * 
	 * @param project
	 *            The project to add the build configuration to
	 * 
	 * @return The newly created configuration
	 * @throws CoreException
	 */
	private Configuration createBuildConfiguration(IProject project)
			throws CoreException {
		ICProjectDescriptionManager descMgr = CoreModel.getDefault()
				.getProjectDescriptionManager();
		ICProjectDescription description = descMgr
				.getProjectDescription(project);
		if (description == null) {
			description = descMgr.createProjectDescription(project, false);
		}
		IManagedBuildInfo managedBuildInfo = ManagedBuildManager
				.getBuildInfo(project);
		if (managedBuildInfo == null) {
			managedBuildInfo = ManagedBuildManager.createBuildInfo(project);
			IProjectType type = ManagedBuildManager
					.getProjectType("cdt.managedbuild.target.gnu.exe");
			ManagedProject mProj = new ManagedProject(project, type);
			managedBuildInfo.setManagedProject(mProj);
		}
		IManagedProject managed = managedBuildInfo.getManagedProject();
		ToolChain tc = (ToolChain) ManagedBuildManager
				.getExtensionToolChain("cdt.managedbuild.toolchain.gnu.base"); //$NON-NLS-1$
		Configuration result = new Configuration((ManagedProject) managed, tc,
				ManagedBuildManager.calculateChildId(tc.getId(), null),
				UNIT_TESTS_BUILD_CONFIGURATION_NAME);

		// install the CDT-standard builder
		String builderID = "org.eclipse.cdt.build.core.settings.default.builder"; //$NON-NLS-1$
		IBuilder builder = ManagedBuildManager.getExtensionBuilder(builderID);
		result.changeBuilder(builder,
				ManagedBuildManager.calculateChildId(result.getId(), null),
				builder.getName());

		// grab the new builder instance and configure it
		builder = result.getEditableBuilder();
		builder.setBuildPath(null);
		builder.setManagedBuildOn(false);
		builder.setBuildAttribute("org.eclipse.cdt.make.core.enableAutoBuild", //$NON-NLS-1$
				"true"); //$NON-NLS-1$
		builder.setBuildAttribute(IMakeBuilderInfo.BUILD_TARGET_INCREMENTAL,
				"all"); //$NON-NLS-1$
		builder.setBuildAttribute(IMakeBuilderInfo.BUILD_TARGET_AUTO, "all"); //$NON-NLS-1$
		builder.setBuildAttribute(IMakeBuilderInfo.BUILD_TARGET_CLEAN, "clean"); //$NON-NLS-1$
		if (_project.equals(project)) {
			builder.setBuildAttribute(IMakeBuilderInfo.BUILD_LOCATION,
					"${workspace_loc:/" + _project.getName() + "/unittests}"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// the config wants an artifact name even though it's a makefile project
		result.setArtifactName(managed.getDefaultArtifactName());

		ICConfigurationDescription cfgDesc = description.createConfiguration(
				ManagedBuildManager.CFG_DATA_PROVIDER_ID,
				result.getConfigurationData());
		// Add the bbmocks include and src folder to the include paths for
		// indexing
		addIncludePaths(cfgDesc, Lists.newArrayList(
				"unittests/bbmocks/include", "unittests/bbmocks/src")); //$NON-NLS-1$ //$NON-NLS-2$
		result.setConfigurationDescription(cfgDesc);

		// Add the binary parser so the unittests executable shows up under
		// the Binaries virtual folder in Project Explorer
		// It seems we have to do it for all configurations or it doesn't work
		// right. Stupid Eclipse
		for (ICConfigurationDescription configDesc : description
				.getConfigurations()) {
			String[] ids = configDesc.getTargetPlatformSetting()
					.getBinaryParserIds();
			List<String> idList = Lists.newArrayList(ids);
			if (!idList.contains(PE_BINARY_PARSER)) {
				idList.add(PE_BINARY_PARSER);
			}
			if (!idList.contains(GNU_ELF_BINARY_PARSER)) {
				idList.add(GNU_ELF_BINARY_PARSER);
			}
			configDesc.getTargetPlatformSetting().setBinaryParserIds(
					idList.toArray(new String[0]));
		}

		descMgr.setProjectDescription(project, description, false, null);

		return result;
	}

	/**
	 * Add a list of include paths to a project configuration description
	 * 
	 * @param configDesc
	 *            The project configuration description to add the include paths
	 *            to
	 * @param paths
	 *            The include paths to add
	 */
	public static void addIncludePaths(ICConfigurationDescription configDesc,
			List<String> paths) {
		ICFolderDescription projectRoot = configDesc.getRootFolderDescription();
		ICLanguageSetting[] settings = projectRoot.getLanguageSettings();
		for (ICLanguageSetting setting : settings) {
			if ((setting.getLanguageId() != null)
					&& (setting.getLanguageId().equals(
							"org.eclipse.cdt.core.g++") //$NON-NLS-1$
							|| setting.getLanguageId().equals(
									"org.eclipse.cdt.core.gcc") //$NON-NLS-1$
					|| setting.getLanguageId().equals(
							"org.eclipse.cdt.core.assembly"))) { //$NON-NLS-1$
				List<ICLanguageSettingEntry> includes = new ArrayList<ICLanguageSettingEntry>();
				includes.addAll(setting
						.getSettingEntriesList(ICSettingEntry.INCLUDE_PATH));
				for (String path : paths) {
					includes.add(new CIncludePathEntry(path,
							ICSettingEntry.VALUE_WORKSPACE_PATH));
				}
				setting.setSettingEntries(ICSettingEntry.INCLUDE_PATH, includes);
			}
		}
	}

	/**
	 * Remove the Unit Tests build configuration from the project
	 * 
	 * @throws CoreException
	 */
	private void removeBuildConfiguration() throws CoreException {
		ICProjectDescriptionManager descMgr = CoreModel.getDefault()
				.getProjectDescriptionManager();
		ICProjectDescription description = descMgr
				.getProjectDescription(_project);

		description.removeConfiguration(UNIT_TESTS_BUILD_CONFIGURATION_NAME);

		descMgr.setProjectDescription(_project, description, false, null);
	}

	/**
	 * Extract the unit testing framework into the project
	 * 
	 * @throws IOException
	 * @throws CoreException
	 */
	private void extractFramework() throws IOException, CoreException {
		Bundle thisBundle = Activator.getDefault().getBundle();
		ZipUtils.extractZip(
				thisBundle.getEntry("files/UnitTestingFramework.zip") //$NON-NLS-1$
						.openStream(), _project.getFolder("unittests")); //$NON-NLS-1$
	}

	/**
	 * Clones the mocks from the server
	 */
	private void cloneMocks() {
		String path = _project.getFolder(Constants.MOCKS_PROJECT_RELATIVE_PATH)
				.getLocation().toString();
		try {
			GitWrapper gitWrapper = GitWrapper.cloneRepository(path,
					Constants.BB_MOCKS_GIT_REMOTE_URL);
			if (gitWrapper == null) {
				DialogUtils.showErrorDialog("Mocks Installation Failed",
						"Cannot clone the mocks repository.");
			} else {
				gitWrapper.closeRepository();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			DialogUtils.showErrorDialog("Mocks Installation Failed",
					e.getMessage());
		}
	}

	/**
	 * Remove the unit test nature from the selected project
	 * 
	 * @throws CoreException
	 */
	private void removeNature() throws CoreException {
		IProjectDescription description;
		description = _project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length - 1];
		int i = 0;
		for (String nature : natures) {
			if (!nature.equals(Constants.UNIT_TEST_FRAMEWORK_NATURE)) {
				newNatures[i] = nature;
				i += 1;
			}
		}
		description.setNatureIds(newNatures);
		_project.setDescription(description, null);
	}

	/**
	 * Add the unit test nature to the selected project
	 * 
	 * @throws CoreException
	 */
	private void addNature() throws CoreException {
		IProjectDescription description;
		description = _project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = Constants.UNIT_TEST_FRAMEWORK_NATURE;
		description.setNatureIds(newNatures);
		_project.setDescription(description, null);
	}

	/**
	 * @return the InstallProperties
	 */
	public InstallProperties getInstallProperties() {
		return _installProperties;
	}

	/**
	 * @param installProperties
	 *            the InstallProperties to set
	 */
	public void setInstallProperties(InstallProperties installProperties) {
		this._installProperties = installProperties;
	}

}
