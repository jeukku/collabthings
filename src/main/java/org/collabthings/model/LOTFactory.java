package org.collabthings.model;

import java.util.Set;

import org.collabthings.PrintOut;
import org.collabthings.math.LVector;

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

	LOTAttachedFactory addFactory(String string);

	LOTAttachedFactory addFactory();

	LOTAttachedFactory getFactory(String string);

	Set<String> getFactories();

	LOTAttachedFactory addFactory(String factoryname, LOTFactory f);

	LOTBoundingBox getBoundingBox();

	void setBoundingBox(LVector LVector3d, LVector LVector3d2);

	void setBoundingBox(LOTBoundingBox lotBoundingBox);

	void setModel(LOT3DModel model);

	void setToolUserSpawnLocation(LVector spawnlocation);

	LVector getToolUserSpawnLocation();

	PrintOut printOut();


}
