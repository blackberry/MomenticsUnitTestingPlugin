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
public class DependencyInformation {
	/**
	 * @author tallen
	 * 
	 */
	public enum Location {
		/**
		 * No location. Usually indicates an uninitiated Location
		 */
		NONE,
		/**
		 * Use the existing installation on the user's computer
		 */
		LOCAL,
		/**
		 * Installed into the project
		 */
		PROJECT;

		/**
		 * @param value
		 *            The string to convert to a Location
		 * @return The Location value, or Location.NONE if invalid
		 */
		public static Location fromString(String value) {
			if (value.toUpperCase().equals(LOCAL.toString())) {
				return Location.LOCAL;
			} else if (value.toUpperCase().equals(PROJECT.toString())) {
				return Location.PROJECT;
			}
			return Location.NONE;
		}
	}

	/**
	 * The location of the Dependency
	 */
	private Location _location;

	/**
	 * The path of the dependency. May be either a directory or a file
	 */
	private String _path;

	/**
	 * Default constructor
	 */
	public DependencyInformation() {
		super();
		this._location = Location.NONE;
		this._path = "";
	}

	/**
	 * Instantiate a DependencyInformation with a location and path
	 * 
	 * @param location
	 *            The location of the Dependency
	 * @param path
	 *            The path to the dependency
	 */
	public DependencyInformation(Location location, String path) {
		super();
		this._location = location;
		this._path = path;
	}

	/**
	 * @return the Location
	 */
	public Location getLocation() {
		return _location;
	}

	/**
	 * @param location
	 *            the Location to set
	 */
	public void setLocation(Location location) {
		this._location = location;
	}

	/**
	 * @return the path of the dependency. May be a file or a directory
	 */
	public String getPath() {
		return _path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this._path = path;
	}
}