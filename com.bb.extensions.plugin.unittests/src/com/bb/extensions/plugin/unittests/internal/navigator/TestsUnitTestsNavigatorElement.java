/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.navigator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.bb.extensions.plugin.unittests.internal.nls.Messages;
import com.bb.extensions.plugin.unittests.internal.utilities.FileUtils;
import com.bb.extensions.plugin.unittests.parsers.HeaderClassParser;
import com.google.common.collect.Lists;

/**
 * @author tallen
 * 
 */
public class TestsUnitTestsNavigatorElement extends
		BaseUnitTestsNavigatorElement implements IWrapsFolder {
	/**
	 * The include folder
	 */
	private static final String INCLUDE_FOLDER = "include"; //$NON-NLS-1$

	/**
	 * The source folder
	 */
	private static final String SOURCE_FOLDER = "src"; //$NON-NLS-1$

	/**
	 * The test folder
	 */
	private static final String TEST_FOLDER = "unittests"; //$NON-NLS-1$

	/**
	 * The tests folder
	 */
	private static final String TESTS_FOLDER = TEST_FOLDER + "/test"; //$NON-NLS-1$

	/**
	 * The parent element
	 */
	TopLevelUnitTestsNavigatorElement _parent;

	/**
	 * Keep track of if we are currently parsing or not
	 */
	private final AtomicBoolean _parsing = new AtomicBoolean(false);

	/**
	 * The list of children underneath the element
	 */
	private List<BaseUnitTestsNavigatorElement> _children = Lists
			.newArrayList();

	/**
	 * The notifier of file changes so we can update the view
	 */
	private IResourceChangeListener _notifier;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent element
	 */
	public TestsUnitTestsNavigatorElement(
			TopLevelUnitTestsNavigatorElement parent) {
		super();

		new InitUI().schedule();

		_parent = parent;

		_notifier = new IResourceChangeListener() {
			@Override
			public void resourceChanged(IResourceChangeEvent event) {
				IResourceDelta projectDelta = event.getDelta().findMember(
						_parent.getProject().getFullPath());

				if ((projectDelta != null)
						&& (projectDelta.getKind() != IResourceDelta.REMOVED)) {
					TestsExtensionsResourceDeltaVisitor visitor = new TestsExtensionsResourceDeltaVisitor();

					try {
						projectDelta.accept(visitor);
					} catch (CoreException e) {
						e.printStackTrace();
					}

					if (visitor.foundMajorChange()) {
						refresh();
					}
				}
			}
		};

		ResourcesPlugin.getWorkspace().addResourceChangeListener(_notifier,
				IResourceChangeEvent.POST_CHANGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getLabel()
	 */
	@Override
	public String getLabel() {
		return Messages.TestsUnitTestsNavigatorElement_label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#getIcon()
	 */
	@Override
	public Object getIcon() {
		try {
			return new URL(
					"platform:/plugin/org.eclipse.cdt.ui/icons/obj16/typedeffo_obj.gif"); //$NON-NLS-1$
		} catch (MalformedURLException e) {
		}
		return null;
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
		synchronized (_children) {
			return _children.toArray();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		synchronized (_children) {
			return !_children.isEmpty();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * IUnitTestsNavigatorElement#kind()
	 */
	@Override
	public Kind kind() {
		return Kind.Tests;
	}

	/**
	 * @author tallen
	 * 
	 */
	private class TestsExtensionsResourceDeltaVisitor implements
			IResourceDeltaVisitor {

		/**
		 * Keep track of if we found a change requiring a full refresh during
		 * visitation
		 */
		private boolean _foundMajorChange = false;

		/**
		 * The source folder
		 */
		private IFolder _sourceFolder;

		/**
		 * The include folder
		 */
		private IFolder _includeFolder;

		/**
		 * The test folder
		 */
		private IFolder _testFolder;

		/**
		 * The tests folder
		 */
		private IFolder _testsFolder;

		/**
		 * Constructor
		 */
		public TestsExtensionsResourceDeltaVisitor() {
			_sourceFolder = _parent.getProject().getFolder(SOURCE_FOLDER);
			_includeFolder = _parent.getProject().getFolder(INCLUDE_FOLDER);
			_testFolder = _parent.getProject().getFolder(TEST_FOLDER);
			_testsFolder = _parent.getProject().getFolder(TESTS_FOLDER);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (_foundMajorChange) {
				// if we have already found a major change quit visiting
				// anything
				return false;
			}
			IResource resource = delta.getResource();
			if (resource.getType() == IResource.PROJECT) {
				// recurse into the project
				return true;
			} else if (resource.getType() == IResource.FOLDER) {
				// only recurse into the required folders and their descendants
				return checkIfEqualOrDescendent(_sourceFolder, resource)
						|| checkIfEqualOrDescendent(_includeFolder, resource)
						|| checkIfEqualOrDescendent(_testFolder, resource);
			} else if (FileUtils.isHeaderFile(resource)) {
				_foundMajorChange = true;
			} else if ((resource.getType() == IResource.FILE)
					&& checkIfEqualOrDescendent(_testsFolder, resource)) {
				if ((delta.getKind() == IResourceDelta.ADDED)
						|| (delta.getKind() == IResourceDelta.REMOVED)) {
					_foundMajorChange = true;
				}
			}
			// don't recurse into anything else
			return false;
		}

		/**
		 * Check if an IResource is equal to or a (direct or indirect)
		 * descendant of another IResource
		 * 
		 * @param ancestor
		 *            The ancestor to check
		 * @param descendant
		 *            The descendant to check
		 * @return True if descendant is equal to or a (direct or indirect)
		 *         descendant of ancestor or false otherwise
		 */
		private boolean checkIfEqualOrDescendent(IResource ancestor,
				IResource descendant) {
			while (descendant != null) {
				if (ancestor.equals(descendant))
					return true;
				descendant = descendant.getParent();
			}
			return false;
		}

		/**
		 * @return True if this visitor found a change requiring refresh or
		 *         false otherwise
		 */
		public boolean foundMajorChange() {
			return _foundMajorChange;
		}
	}

	/**
	 * @author tallen
	 * 
	 */
	private class InitUI extends Job {
		/**
		 * The runnable used to update the tree in a safe thread
		 */
		private final Runnable _updater = new Runnable() {

			public synchronized void run() {
				_parent.refresh();
			}
		};

		/**
		 * Constructor
		 */
		InitUI() {
			super(Messages.TestsUnitTestsNavigatorElement_refreshJobName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
		 * IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (!_parsing.getAndSet(true)) {
				IProject project = _parent.getProject();

				List<IFile> headerFiles = FileUtils.findHeaderFiles(project
						.getFolder(SOURCE_FOLDER));
				headerFiles.addAll(FileUtils.findHeaderFiles(project
						.getFolder(INCLUDE_FOLDER)));

				HeaderClassParser parser = new HeaderClassParser();
				List<ParsedClassInformation> parsedClasses = Lists
						.newArrayList();

				for (IFile file : headerFiles) {
					parsedClasses.addAll(parser.parse(file));
				}

				for (ParsedClassInformation pci : parsedClasses) {
					TestClassUnitTestsNavigatorElement element = new TestClassUnitTestsNavigatorElement(
							pci, TestsUnitTestsNavigatorElement.this);
					synchronized (_children) {
						_children.add(element);
					}
					refresh();
				}

				// now find source files in the test folder that does not match
				// up to a given class
				List<IFile> sourceFiles = FileUtils.findSourceFiles(project
						.getFolder(TESTS_FOLDER));
				for (IFile sourceFile : sourceFiles) {
					if (!sourceIsAccountedFor(parsedClasses, sourceFile)) {
						// we need to add it to the unknown list
						UnknownTestFileUnitTestsNavigatorElement element = new UnknownTestFileUnitTestsNavigatorElement(
								sourceFile, TestsUnitTestsNavigatorElement.this);
						synchronized (_children) {
							_children.add(element);
						}
						refresh();
					}
				}

				refresh();
				_parsing.set(false);
			}

			return Status.OK_STATUS;
		}

		/**
		 * Test if a source file has been accounted for in the list of parsed
		 * classes
		 * 
		 * @param parsedClasses
		 *            The list of parsed classes
		 * @param file
		 *            The test file to check
		 * @return True if the test file is accounted for in parsedClasses or
		 *         false otherwise
		 */
		private boolean sourceIsAccountedFor(
				List<ParsedClassInformation> parsedClasses, IFile file) {
			for (ParsedClassInformation pci : parsedClasses) {
				if (pci.getTestFile().equals(file)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Refresh the tree inside a new display-safe thread
		 */
		private void refresh() {
			Display.getDefault().asyncExec(_updater);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bb.extensions.plugin.unittests.internal.navigator.
	 * BaseUnitTestsNavigatorElement#refresh()
	 */
	@Override
	public void refresh() {
		if (!_parsing.get()) {
			synchronized (_children) {
				_children.clear();
			}
			new InitUI().schedule();
		} else {
			super.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bb.extensions.plugin.unittests.internal.navigator.IWrapsFolder#getFolder
	 * ()
	 */
	@Override
	public IFolder getFolder() {
		return _parent.getProject().getFolder(TESTS_FOLDER);
	}
}
