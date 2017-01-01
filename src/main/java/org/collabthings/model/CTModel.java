package org.collabthings.model;

import java.io.File;
import java.io.IOException;

import org.collabthings.CTListener;

import com.jme3.math.Vector3f;

import waazdoh.common.WObjectID;

public interface CTModel {

	String SCAD = "openscad";
	String HEIGHTMAP = "heightmap";
	String BINARY = "binary";

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
