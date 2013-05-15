/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.utilities;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

/**
 * @author tallen
 * 
 */
public class ZipUtils {
	/**
	 * Extract a zip file to a folder
	 * 
	 * @param zipFile
	 *            The zip file to extract
	 * @param installFolder
	 *            The folder to extract to
	 * @return true if the extraction completed successfully false otherwise
	 * @throws IOException
	 */
	public static boolean extractZip(File zipFile, String installFolder)
			throws IOException {
		return extractZip(new FileInputStream(zipFile), installFolder);
	}

	/**
	 * Extract a zip file to a folder
	 * 
	 * @param zipStream
	 *            The zip stream to extract
	 * @param installFolder
	 *            The folder to extract to
	 * @return true if the extraction completed successfully false otherwise
	 * @throws IOException
	 */
	public static boolean extractZip(InputStream zipStream, String installFolder)
			throws IOException {
		// Second, extract the zip
		ZipInputStream zipInputStream = null;
		ZipEntry zipEntry;
		zipInputStream = new ZipInputStream(zipStream);

		File installLocation = new File(installFolder);

		zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			if (zipEntry.isDirectory()) {
				File dir = new File(installLocation, zipEntry.getName());
				if (!dir.getParentFile().exists()
						&& !dir.getParentFile().mkdirs()) {
					return false;
				}
			} else {
				File file = new File(installLocation, zipEntry.getName());
				if (!file.getParentFile().exists()
						&& !file.getParentFile().mkdirs()) {
					return false;
				}

				BufferedOutputStream bos;
				bos = new BufferedOutputStream(new FileOutputStream(file));
				byte[] bytesIn = new byte[4096];
				int read = 0;
				while ((read = zipInputStream.read(bytesIn)) != -1) {
					bos.write(bytesIn, 0, read);
				}
				bos.close();

				zipInputStream.closeEntry();
			}

			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();

		return true;
	}

	/**
	 * Extract a zip file to a folder
	 * 
	 * @param zipSource
	 *            The location of the zip file to install
	 * @param installFolder
	 *            The folder to install to
	 * @throws IOException
	 * @throws CoreException
	 */
	public static void extractZip(String zipSource, IFolder installFolder)
			throws IOException, CoreException {
		extractZip(new FileInputStream(zipSource), installFolder);
	}

	/**
	 * Extract a zip file to a folder
	 * 
	 * @param zipSource
	 *            The input stream of the zip file to install
	 * @param installFolder
	 *            The folder to install to
	 * @throws IOException
	 * @throws CoreException
	 */
	public static void extractZip(InputStream zipSource, IFolder installFolder)
			throws IOException, CoreException {
		ZipInputStream zipInputStream = null;
		ZipEntry zipEntry;
		zipInputStream = new ZipInputStream(zipSource);

		zipEntry = zipInputStream.getNextEntry();
		while (zipEntry != null) {
			if (zipEntry.isDirectory()) {
				FileUtils.create(installFolder.getFolder(zipEntry.getName()));
			} else {
				IFile file = installFolder.getFile(zipEntry.getName());
				// create and setContents both close the input stream when
				// they are done. Why? Because Eclipse hates us. Therefore
				// we have to wrap the zipInputStream in our own wrapper so
				// it can close the wrapper instead of the actual
				// zipInputStream
				if (!file.exists()) {
					file.create(new InputStreamWrapper(zipInputStream), true,
							null);
				} else {
					file.setContents(new InputStreamWrapper(zipInputStream),
							true, true, null);
				}
				zipInputStream.closeEntry();
			}

			zipEntry = zipInputStream.getNextEntry();
		}

		zipInputStream.close();
	}

	/**
	 * @param directory
	 * @param zipfile
	 * @throws IOException
	 */
	public static void zip(File directory, File zipfile) throws IOException {
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<File>();
		queue.push(directory);
		OutputStream out = new FileOutputStream(zipfile);
		Closeable res = out;
		try {
			ZipOutputStream zout = new ZipOutputStream(out);
			res = zout;
			while (!queue.isEmpty()) {
				directory = queue.pop();
				for (File kid : directory.listFiles()) {
					String name = base.relativize(kid.toURI()).getPath();
					if (kid.isDirectory()) {
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					} else {
						zout.putNextEntry(new ZipEntry(name));
						copy(kid, zout);
						zout.closeEntry();
					}
				}
			}
		} finally {
			res.close();
		}
	}

	/**
	 * @param zipfile
	 * @param directory
	 * @throws IOException
	 */
	public static void unzip(File zipfile, File directory) throws IOException {
		ZipFile zfile = new ZipFile(zipfile);
		Enumeration<? extends ZipEntry> entries = zfile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File file = new File(directory, entry.getName());
			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				file.getParentFile().mkdirs();
				InputStream in = zfile.getInputStream(entry);
				try {
					copy(in, file);
				} finally {
					in.close();
				}
			}
		}
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}

	/**
	 * @param file
	 * @param out
	 * @throws IOException
	 */
	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			copy(in, out);
		} finally {
			in.close();
		}
	}

	/**
	 * @param in
	 * @param file
	 * @throws IOException
	 */
	private static void copy(InputStream in, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);
		} finally {
			out.close();
		}
	}
}
