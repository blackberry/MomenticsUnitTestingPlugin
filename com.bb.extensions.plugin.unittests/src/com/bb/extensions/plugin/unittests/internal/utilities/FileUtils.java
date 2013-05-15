/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.utilities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author tallen
 * 
 */
public class FileUtils {
	/**
	 * Recursively create IResources above the parameter in the parent tree
	 * 
	 * @param resource
	 * @throws CoreException
	 */
	public static void create(final IResource resource) throws CoreException {
		if (resource == null || resource.exists()) {
			return;
		}
		if (!resource.getParent().exists()) {
			create(resource.getParent());
		}
		switch (resource.getType()) {
		case IResource.FILE:
			((IFile) resource).create(new ByteArrayInputStream(new byte[0]),
					true, null);
			break;
		case IResource.FOLDER:
			((IFolder) resource).create(IResource.NONE, true, null);
			break;
		case IResource.PROJECT:
			((IProject) resource).create(null);
			((IProject) resource).open(null);
			break;
		}
	}

	/**
	 * Copy sourceFile to destFile
	 * 
	 * @param sourceFile
	 *            The source file to copy from
	 * @param destFile
	 *            The destination file to copy to
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	/**
	 * Extract the filename from a (relative or absolute) path. Works with both
	 * / and \ separators.
	 * 
	 * @param path
	 *            The path to extract the filename from
	 * @return The filename portion of path
	 */
	public static String extractFilename(String path) {
		String result = path.substring(path.lastIndexOf('/') + 1);
		return result.substring(result.lastIndexOf('\\') + 1);
	}

	/**
	 * Delete the File/Directory, all files underneath (if it is a Directory)
	 * and all parent Directories which are empty.
	 * 
	 * @param dir
	 *            The Directory to delete
	 * @return True if the File was deleted successfully false otherwise
	 */
	public static boolean deleteFileAndEmptyParentDirectories(File dir) {
		File parentDirectory = dir.getParentFile();
		if (!deleteDirectoryRecursive(dir)) {
			return false;
		}
		while (true) {
			dir = parentDirectory;
			parentDirectory = dir.getParentFile();
			if (!deleteDirectoryIfEmpty(dir)) {
				break;
			}
		}
		return true;
	}

	/**
	 * Delete the directory and all files underneath.
	 * 
	 * @param dir
	 *            The directory to delete including all its children
	 * @return True if the directory was deleted successfully false otherwise
	 */
	public static boolean deleteDirectoryRecursive(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectoryRecursive(new File(dir,
						children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * Delete the directory if and only if it is empty
	 * 
	 * @param dir
	 *            The directory to delete
	 * @return True if the directory was deleted, false otherwise
	 */
	private static boolean deleteDirectoryIfEmpty(File dir) {
		if (dir.isDirectory() && (dir.list().length > 0)) {
			return false;
		} else {
			return dir.delete();
		}
	}

	/**
	 * Convert an input stream to a string
	 * 
	 * @param stream
	 *            The input stream
	 * @return The string contents of the input stream
	 */
	public static String toString(java.io.InputStream stream) {
		Scanner s = new Scanner(stream).useDelimiter("\\A"); //$NON-NLS-1$
		return s.hasNext() ? s.next() : ""; //$NON-NLS-1$
	}

	/**
	 * Check if a file exists
	 * 
	 * @param file
	 *            The path to the file/directory
	 * @return True if the file/directory exists or false otherwise
	 */
	public static boolean exists(String file) {
		return (new File(file)).exists();
	}

	/**
	 * Test if the resource is a header file. Currently this uses an extension
	 * test (h and hpp).
	 * 
	 * @param resource
	 *            The resource to test
	 * @return True if the resource is a header file false otherwise
	 */
	public static boolean isHeaderFile(IResource resource) {
		String extension = resource.getFileExtension();
		return "h".equals(extension) || "hpp".equals(extension); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Test if the resource is a C++ source file. Currently this uses an
	 * extension test (cpp and cc).
	 * 
	 * @param resource
	 *            The resource to test
	 * @return True if the resource is a C++ source file false otherwise
	 */
	public static boolean isSourceFile(IResource resource) {
		String extension = resource.getFileExtension();
		return "cpp".equals(extension) || "cc".equals(extension) || "cxx".equals(extension); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Find all header files recursively under a given folder
	 * 
	 * @param folder
	 *            The folder to search
	 * @return A list of header file objects
	 */
	public static List<IFile> findHeaderFiles(IFolder folder) {
		List<String> extensions = new ArrayList<String>();
		extensions.add("h");
		extensions.add("hpp");
		return findFiles(folder, extensions);
	}

	/**
	 * Finds all the files in the given folder with the given extensions.
	 * 
	 * @param folder
	 *            The folder to search
	 * @param extensions
	 *            The extensions to look for.
	 * @return The list of files found.
	 */
	public static List<IFile> findFiles(IFolder folder,
			final List<String> extensions) {
		final List<IFile> files = new ArrayList<IFile>();

		try {
			folder.accept(new IResourceVisitor() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.core.resources.IResourceVisitor#visit(org
				 * .eclipse .core.resources.IResource)
				 */
				@Override
				public boolean visit(IResource res) throws CoreException {
					if (res.getType() == IResource.FILE) {
						if (extensions.contains(res.getFileExtension())) {
							files.add((IFile) res);
						}
						return false;
					}
					return true;
				}

			});
		} catch (CoreException e1) {
			// on exception, return nothing
			files.clear();
		}

		return files;
	}

	/**
	 * Finds all the files in the given folder.
	 * 
	 * @param folder
	 *            The folder to search
	 * @return The list of files found.
	 */
	public static List<IFile> findFiles(IFolder folder) {
		final List<IFile> files = new ArrayList<IFile>();

		try {
			folder.accept(new IResourceVisitor() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.core.resources.IResourceVisitor#visit(org
				 * .eclipse .core.resources.IResource)
				 */
				@Override
				public boolean visit(IResource res) throws CoreException {
					if (res.getType() == IResource.FILE) {
						files.add((IFile) res);
						return false;
					}
					return true;
				}

			});
		} catch (CoreException e1) {
			// on exception, return nothing
			files.clear();
		}

		return files;
	}

	/**
	 * Find all source files recursively under a given folder
	 * 
	 * @param folder
	 *            The folder to search
	 * @return A list of source file objects
	 */
	public static List<IFile> findSourceFiles(IFolder folder) {
		List<String> extensions = new ArrayList<String>();
		extensions.add("cpp");
		extensions.add("cxx");
		extensions.add("cc");
		return findFiles(folder, extensions);
	}

	/**
	 * Copy srcDir with all its contents to destDir.
	 * 
	 * @param srcDir
	 * @param destDir
	 * @throws IOException
	 */
	public static void copyDirectory(File srcDir, File destDir)
			throws IOException {
		org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir);
	}

	/**
	 * Create a temp directory in a folder
	 * 
	 * @param file
	 *            The folder to make the temp dir in
	 * @return The temp directory created
	 */
	public static File findTempFolder(File file) {
		String baseName = System.currentTimeMillis() + "-";
		for (int counter = 0; counter < 1000; counter++) {
			File tempDir = new File(file, baseName + counter);
			if (tempDir.mkdir()) {
				return tempDir;
			}
		}
		return null;
	}
}
