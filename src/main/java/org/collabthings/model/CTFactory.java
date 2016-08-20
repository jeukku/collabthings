package org.collabthings.model;

import java.util.Set;

import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

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

	void setBoundingBox(Vector3f Vector3f3d, Vector3f Vector3f3d2);

	void setBoundingBox(CTBoundingBox ctBoundingBox);

	void setModel(CTBinaryModel model);

	void setToolUserSpawnLocation(Vector3f spawnlocation);

	Vector3f getToolUserSpawnLocation();

	PrintOut printOut();

}
