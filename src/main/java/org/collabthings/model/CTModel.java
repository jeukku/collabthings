package org.collabthings.model;

import java.io.File;
import java.io.IOException;

import org.collabthings.math.LVector;
import org.collabthings.scene.CTGroup;

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

	LVector getTranslation();

	void setTranslation(LVector translation);

	void addTo(CTGroup g);

}
