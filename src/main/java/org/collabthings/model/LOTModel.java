package org.collabthings.model;

import java.io.File;

import waazdoh.common.ObjectID;

public interface LOTModel {

	ObjectID getID();

	void publish();

	void save();

	boolean isReady();

	LOTTriangleMesh getTriangleMesh();

	boolean importModel(File file);

	String getModelType();
}
