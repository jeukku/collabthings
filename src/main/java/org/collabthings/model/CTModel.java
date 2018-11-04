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
package org.collabthings.model;

import java.io.File;
import java.io.IOException;

import org.collabthings.CTListener;

import com.jme3.math.Vector3f;

import collabthings.datamodel.WObjectID;

public interface CTModel {

	WObjectID getID();

	void publish();

	void save();

	boolean isReady();

	CTTriangleMesh getTriangleMesh();

	boolean importModel(File file) throws IOException;

	String getModelType();

	double getScale();

	void setScale(double scale);

	Vector3f getTranslation();

	void setTranslation(Vector3f translation);

	boolean isDisabled();

	void setDisabled(boolean b);

	void addChangeListener(CTListener l);

	long getModified();

}
