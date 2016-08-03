package org.collabthings.model;

import java.io.File;
import java.io.IOException;

import org.collabthings.CTListener;
import org.collabthings.scene.CTGroup;

import com.jme3.math.Vector3f;

import waazdoh.common.ObjectID;

public interface CTModel {

	String SCAD = "openscad";

	ObjectID getID();

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

	void addTo(CTGroup g);

	boolean isDisabled();

	void setDisabled(boolean b);

	void addChangeListener(CTListener l);

}
