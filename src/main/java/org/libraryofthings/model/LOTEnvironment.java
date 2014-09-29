package org.libraryofthings.model;

import org.libraryofthings.math.LVector;

import waazdoh.client.model.MID;

public interface LOTEnvironment extends LOTObject {

	LOTScript getScript(String string);

	void addScript(String scriptname, LOTScript lotScript);

	void addTool(String string, LOTTool partsource);

	LOTTool getTool(String string);

	void setParameter(String string, MID id);

	void setParameter(String string, String value);

	String getParameter(String string);

	void setVectorParameter(String string, LVector lVector);

	LVector getVectorParameter(String name);

	boolean isReady();

	MID getID();

}
