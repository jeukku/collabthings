package org.libraryofthings.model;

import java.util.Set;

import org.libraryofthings.PrintOut;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;

import waazdoh.common.ObjectID;

public interface LOTFactory extends LOTObject {

	LOTScript getScript(String string);

	LOTEnvironment getEnvironment();

	void save();

	void publish();

	ObjectID getID();

	String getName();

	void setName(String string);

	LOTScript addScript(String string, LOTScript lotScript);

	LOTScript addScript(String string);

	Set<String> getScripts();

	LOTFactory addFactory(String string);

	LOTFactory addFactory();

	LOTFactory getFactory(String string);

	Set<String> getFactories();

	LOTFactory addFactory(String factoryname, LOTFactory f);

	void setLocation(LVector LVector3d);

	void setOrientation(LVector lVector, double d);

	LOTBoundingBox getBoundingBox();

	void setBoundingBox(LVector LVector3d, LVector LVector3d2);

	void setBoundingBox(LOTBoundingBox lotBoundingBox);

	void setModel(LOT3DModel model);

	void setToolUserSpawnLocation(LVector spawnlocation);

	LVector getToolUserSpawnLocation();

	LTransformation getTransformation();

	PrintOut printOut();

}
