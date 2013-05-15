/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

/**
 * @author tallen
 * 
 */
public abstract class TestFileUnitTestsNavigatorElement extends
		BaseUnitTestsNavigatorElement {
	/**
	 * The parent
	 */
	private TestsUnitTestsNavigatorElement _parent;

	/**
	 * The count of the number of tests in the test file
	 */
	private int _testCount;

	/**
	 * The notifier of file changes so we can update the view
	 */
	private IResourceChangeListener _notifier;

	/**
	 * The test file associated with this node
	 */
	private IFile _testFile;

	/**
	 * Constructor
	 * 
	 * @param testFile
	 *            The test file associated with this node
	 * @param parent
	 *            The parent for this element
	 */
	public TestFileUnitTestsNavigatorElement(IFile testFile,
			TestsUnitTestsNavigatorElement parent) {
		super();

		_testFile = testFile;
		_parent = parent;
		_testCount = 0;

		parseTestFile();

		_notifier = new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				IResourceDelta projectDelta = event.getDelta().findMember(
						_testFile.getFullPath());

				if (projectDelta != null) {
					parseTestFile();
					Display.getDefault().asyncExec(new Runnable() {
						/*
						 * (non-Javadoc)
						 * 
						 * @see java.lang.Runnable#run()
						 */
						@Override
						public void run() {
							update(null);
						}
					});
				}
			}
		};

		ResourcesPlugin.getWorkspace().addResourceChangeListener(_notifier,
				IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Parse the test file counting the number of tests found
	 */
	private void parseTestFile() {
		_testCount = 0;
		if (!_testFile.exists()) {
			// the test file does not exist
			return;
		}
		Pattern pattern = Pattern.compile("\\s+TEST(_F)?\\s*\\("); //$NON-NLS-1$
		try {
			InputStream contents = _testFile.getContents();
			try {
				Scanner scanner = new Scanner(contents);
				String match = scanner.findWithinHorizon(pattern, 0);
				while (match != null) {
					_testCount += 1;
					match = scanner.findWithinHorizon(pattern, 0);
				}
			} finally {
				contents.close();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getParent()
	 */
	@Override
	public Object getParent() {
		return _parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getChildren()
	 */
	@Override
	public Object[] getChildren() {
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/**
	 * @return The test file associated with this node
	 */
	public IFile getTestFile() {
		return _testFile;
	}

	/**
	 * @return The test count found in the file
	 */
	protected int getTestCount() {
		return _testCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#kind()
	 */
	@Override
	public Kind kind() {
		return Kind.TestFile;
	}
}
