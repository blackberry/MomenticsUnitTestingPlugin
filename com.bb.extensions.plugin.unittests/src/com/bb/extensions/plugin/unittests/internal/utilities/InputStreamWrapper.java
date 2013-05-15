/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.utilities;

import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper around
 * 
 * @author tallen
 * 
 */
public class InputStreamWrapper extends InputStream {

	/**
	 * The InputStream to wrap around
	 */
	private InputStream _inputStream;

	/**
	 * Create the wrapper with an InputStream
	 * 
	 * @param inputStream
	 *            The InputStream to wrap around
	 */
	public InputStreamWrapper(InputStream inputStream) {
		_inputStream = inputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return _inputStream.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return _inputStream.available();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int arg0) {
		_inputStream.mark(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return _inputStream.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return _inputStream.read(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] arg0) throws IOException {
		return _inputStream.read(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		_inputStream.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long arg0) throws IOException {
		return _inputStream.skip(arg0);
	}

}
