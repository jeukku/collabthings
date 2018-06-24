/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.model.impl;

public enum CTConstants {
	;

	// client constants
	public static final String VERSION = "0.0.3";
	public static final String LOWEST_ACCEPTED_VERSION = "0.0.3";
	public static final String CHARSET = "UTF-8";

	public static final String VALUE_TYPE_X3D = "x3d";
	public static final String VALUE_TYPE_STL = "stl";
	public static final String VALUE_TYPE_BINARY = "binary";
	
	// object types
	public static final String MAPOFPIECES = "mapofobjects";
	
	// model types
	public static final String MODELTYPE_SCAD = "openscad";
	public static final String MODELTYPE_HEIGHTMAP = "heightmap";
	public static final String MODELTYPE_BINARY = "binary";
	
	// preferences
	public static final String JAVASCRIPT_FORBIDDENWORDS = "ct.javaapplication.forbiddenwords";
	public static final String PREFERENCES_OPENSCADPATH = "software.openscad.path";
	public static final String ERROR_OPENSCADFAILED = "error_openscadfailed";

}
