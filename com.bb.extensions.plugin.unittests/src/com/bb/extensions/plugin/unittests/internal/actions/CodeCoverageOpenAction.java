/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
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

import com.bb.extensions.plugin.unittests.Activator;
import com.bb.extensions.plugin.unittests.internal.navigator.CodeCoverageUnitTestsNavigatorElement;
import com.bb.extensions.plugin.unittests.internal.nls.Messages;
import com.bb.extensions.plugin.unittests.internal.utilities.DialogUtils;

/**
 * @author tallen
 * 
 */
public class CodeCoverageOpenAction extends BaseSelectionListenerAction {

	/**
	 * @author tallen
	 * 
	 */
	public class GcdaResourceVisitor implements IResourceVisitor {
		/**
		 * The file we found
		 */
		private IFile _file;

		/**
		 * Constructor
		 */
		public GcdaResourceVisitor() {
			super();
			_file = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core
		 * .resources.IResource)
		 */
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (_file != null) {
				return false;
			}
			if ("gcda".equalsIgnoreCase(resource.getFileExtension())) { //$NON-NLS-1$
				_file = (IFile) resource;
				return false;
			}

			return true;
		}

		/**
		 * @return The file we found
		 */
		public IFile getFile() {
			return _file;
		}
	}

	/**
	 * The default open action
	 */
	private final BaseSelectionListenerAction _openAction;

	/**
	 * The currently selected help item
	 */
	private CodeCoverageUnitTestsNavigatorElement _selectedElement;

	/**
	 * @param commonOpen
	 *            The default open action
	 */
	public CodeCoverageOpenAction(BaseSelectionListenerAction commonOpen) {
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
		try {
			// It doesn't seem like the project is being refreshed after a unit
			// test run, so we need to refresh the project here so that we can
			// find the gcda file
			_selectedElement.getProject().refreshLocal(
					IResource.DEPTH_INFINITE, new NullProgressMonitor() {
						/**
						 * Keep track of if this is the first call to done()
						 */
						private boolean firstDone = true;

						@Override
						public void done() {
							if (firstDone) {
								firstDone = false;
								performAction();
							}
						}
					});
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Perform the open action
	 */
	private void performAction() {
		if (_selectedElement != null) {
			IProject project = _selectedElement.getProject();
			IFolder utFolder = project.getFolder("unittests");
			if (utFolder.exists() && utFolder.isLinked()) {
				String utProjectName = project.getName() + "unittests";
				final IProject utProject = _selectedElement.getProject()
						.getWorkspace().getRoot().getProject(utProjectName);
				if (utProject.exists() == false) {
					File projectParentFolder = _selectedElement.getProject()
							.getLocation().toFile().getParentFile();
					final File unittestsProjectFolder = new File(
							projectParentFolder, "unittests");
					if (unittestsProjectFolder.exists()) {
						Job job = new Job("Open Code Coverage") {
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									IProjectDescription utDesc = ResourcesPlugin
											.getWorkspace()
											.loadProjectDescription(
													new Path(
															new File(
																	unittestsProjectFolder,
																	".project")
																	.getPath()));
									utProject.create(utDesc,
											new NullProgressMonitor());
									utProject.open(new NullProgressMonitor());
								} catch (CoreException e) {
									e.printStackTrace();
								}
								return Status.OK_STATUS;
							}
						};
						job.addJobChangeListener(new IJobChangeListener() {

							@Override
							public void aboutToRun(IJobChangeEvent event) {
							}

							@Override
							public void awake(IJobChangeEvent event) {
							}

							@Override
							public void done(IJobChangeEvent event) {
								Display.getDefault().syncExec(new Runnable() {
									@Override
									public void run() {
										performOpenAction();
									}
								});
							}

							@Override
							public void running(IJobChangeEvent event) {
							}

							@Override
							public void scheduled(IJobChangeEvent event) {
							}

							@Override
							public void sleeping(IJobChangeEvent event) {
							}

						});
						job.setUser(true);
						job.schedule();
					} else {
						List<IStatus> statuses = new ArrayList<IStatus>(1);
						statuses.add(new Status(IStatus.ERROR,
								Activator.PLUGIN_ID, 1,
								"Unit testing project not found", null));
						DialogUtils.showErrorDialog(Activator.PLUGIN_ID,
								statuses);
					}
				} else {
					performOpenAction();
				}
			} else {
				performOpenAction();
			}
		}
	}

	/**
	 * perform the open action
	 */
	private void performOpenAction() {
		IFolder cmakeFolder = _selectedElement.getProject().getFolder(
				"/unittests/build/CMakeFiles/");
		this.deleteCMakeTmpFolder(cmakeFolder);
		IFile file = getCodeCoverageFile();

		if (file != null && file.exists()) {
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
		} else {
			List<IStatus> statuses = new ArrayList<IStatus>(1);
			statuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 1,
					Messages.CodeCoverageOpenAction_noCodeCoverageFilesError,
					null));
			DialogUtils.showErrorDialog(Activator.PLUGIN_ID, statuses);
		}
	}

	/**
	 * Searches the given folder recursively and deletes the folder "2.8.10.2"
	 * or similar generated by CMake because GCov is picking the binaries from
	 * it first.
	 * 
	 * @param folder
	 * @return true if the folder has been found and deleted in the given
	 *         folder.
	 */
	private boolean deleteCMakeTmpFolder(IFolder folder) {
		try {
			for (IResource res : folder.members()) {
				if (res.getType() == IResource.FOLDER) {
					if (this.isCMakeVersionFolder(res.getName())) {
						res.delete(true, null);
						return true;
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param text
	 * @return true if the string contains numbers separated by dots (at least
	 *         three dots). e.g "2.8.10.2".
	 */
	private boolean isCMakeVersionFolder(String text) {
		String[] tokens = text.split("\\.");
		if (tokens.length < 3) {
			return false;
		}
		if (tokens != null) {
			for (String token : tokens) {
				try {
					Integer.parseInt(token);
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return An IFile pointing to a gcda file
	 */
	private IFile getCodeCoverageFile() {
		IFolder folder = _selectedElement.getProject().getFolder(
				CodeCoverageUnitTestsNavigatorElement.GCDA_FOLDER);

		GcdaResourceVisitor visitor = new GcdaResourceVisitor();

		try {
			folder.accept(visitor);
		} catch (CoreException e) {
		}

		return visitor.getFile();
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
			if (selected instanceof CodeCoverageUnitTestsNavigatorElement) {
				_selectedElement = (CodeCoverageUnitTestsNavigatorElement) selected;
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
