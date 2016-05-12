package org.collabthings.model;

import java.io.File;

import org.collabthings.math.LVector;
import org.collabthings.scene.CTGroup;

import waazdoh.common.ObjectID;

public interface LOTModel {

	String SCAD = "openscad";

	ObjectID getID();

	void publish();

	void save();

	boolean isReady();

	LOTTriangleMesh getTriangleMesh();

	boolean importModel(File file);

	String getModelType();

	double getScale();

	void setScale(double scale);

	LVector getTranslation();

	void setTranslation(LVector translation);

	void addTo(CTGroup g);
}
