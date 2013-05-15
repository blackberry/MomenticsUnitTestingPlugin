/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.data;

/**
 * @author tallen
 * 
 */
public class InstallProperties {
	/**
	 * The build system to install
	 */
	private BuildSystem _buildSystem;

	/**
	 * The test framework to install
	 */
	private TestFramework _testFramework;

	/**
	 * The CMake dependency
	 */
	private DependencyInformation _cmakeDependency = new DependencyInformation();

	/**
	 * The QMake dependency
	 */
	private DependencyInformation _qmakeDependency = new DependencyInformation();

	/**
	 * The MinGW dependency
	 */
	private DependencyInformation _mingwDependency = new DependencyInformation();

	/**
	 * The MSys dependency
	 */
	private DependencyInformation _msysDependency = new DependencyInformation();

	/**
	 * @author tallen
	 * 
	 */
	public enum BuildSystem {
		/**
		 * Use the CMake build system
		 */
		CMAKE
	}

	/**
	 * @author tallen
	 * 
	 */
	public enum TestFramework {
		/**
		 * Use GTest with GMock
		 */
		GTEST_GMOCK
	}

	/**
	 * @return the BuildSystem
	 */
	public BuildSystem getBuildSystem() {
		return _buildSystem;
	}

	/**
	 * @param buildSystem
	 *            the BuildSystem to set
	 */
	public void setBuildSystem(BuildSystem buildSystem) {
		this._buildSystem = buildSystem;
	}

	/**
	 * @return the TestFramework
	 */
	public TestFramework getTestFramework() {
		return _testFramework;
	}

	/**
	 * @param testFramework
	 *            the TestFramework to set
	 */
	public void setTestFramework(TestFramework testFramework) {
		this._testFramework = testFramework;
	}

	/**
	 * @return the CMake Dependency
	 */
	public DependencyInformation getCmakeDependency() {
		return _cmakeDependency;
	}

	/**
	 * @param cmakeDependency
	 *            the CMake Dependency to set
	 */
	public void setCmakeDependency(DependencyInformation cmakeDependency) {
		this._cmakeDependency = cmakeDependency;
	}

	/**
	 * @return the QMake Dependency
	 */
	public DependencyInformation getQmakeDependency() {
		return _qmakeDependency;
	}

	/**
	 * @param qmakeDependency
	 *            the QMake Dependency to set
	 */
	public void setQmakeDependency(DependencyInformation qmakeDependency) {
		this._qmakeDependency = qmakeDependency;
	}

	/**
	 * @return the MinGW Dependency
	 */
	public DependencyInformation getMingwDependency() {
		return _mingwDependency;
	}

	/**
	 * @param mingwDependency
	 *            the MinGW Dependency to set
	 */
	public void setMingwDependency(DependencyInformation mingwDependency) {
		this._mingwDependency = mingwDependency;
	}

	/**
	 * @return the MSys Dependency
	 */
	public DependencyInformation getMsysDependency() {
		return _msysDependency;
	}

	/**
	 * @param msysDependency
	 *            the MSys Dependency to set
	 */
	public void setMsysDependency(DependencyInformation msysDependency) {
		this._msysDependency = msysDependency;
	}

}
