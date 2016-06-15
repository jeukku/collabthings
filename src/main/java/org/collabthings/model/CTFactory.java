package org.collabthings.model;

import java.util.Set;

import org.collabthings.math.LVector;
import org.collabthings.util.PrintOut;

public interface CTFactory extends CTObject {

	CTScript getScript(String string);

	CTEnvironment getEnvironment();

	String getName();

	void setName(String string);

	CTScript addScript(String string, CTScript ctScript);

	CTScript addScript(String string);

	Set<String> getScripts();

	CTAttachedFactory addFactory(String string);

	CTAttachedFactory addFactory();

	CTAttachedFactory getFactory(String string);

	Set<String> getFactories();

	CTAttachedFactory addFactory(String factoryname, CTFactory f);

	CTBoundingBox getBoundingBox();

	void setBoundingBox(LVector LVector3d, LVector LVector3d2);

	void setBoundingBox(CTBoundingBox ctBoundingBox);

	void setModel(CTBinaryModel model);

	void setToolUserSpawnLocation(LVector spawnlocation);

	LVector getToolUserSpawnLocation();

	PrintOut printOut();

}
