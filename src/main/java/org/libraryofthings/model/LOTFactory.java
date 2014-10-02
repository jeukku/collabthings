package org.libraryofthings.model;

import java.util.Set;

import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;

import waazdoh.client.model.MID;

public interface LOTFactory extends LOTObject {

	LOTScript getScript(String string);

	LOTEnvironment getEnvironment();

	void save();

	MID getID();

	void setName(String string);

	LOTScript addScript(String string, LOTScript lotScript);

	void publish();

	String getName();

	LOTScript addScript(String string);

	LOTFactory addFactory(String string);

	LOTFactory getFactory(String string);

	Set<String> getFactories();

	void setLocation(LVector LVector3d);

	void setOrientation(LVector lVector, double d);

	LOTBoundingBox getBoundingBox();

	void setBoundingBox(LVector LVector3d, LVector LVector3d2);

	void setModel(LOT3DModel model);

	void setToolUserSpawnLocation(LVector spawnlocation);

	LVector getToolUserSpawnLocation();

	LTransformation getTransformation();

}
