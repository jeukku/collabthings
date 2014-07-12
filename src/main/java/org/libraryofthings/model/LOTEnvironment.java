package org.libraryofthings.model;

import waazdoh.client.model.MID;

public interface LOTEnvironment {

	LOTScript getScript(String string);

	void addScript(String scriptname, LOTScript lotScript);

	void save();

	void publish();

	void addTool(String string, LOTTool partsource);

	LOTTool getTool(String string);

	void setParameter(String string, MID id);

	void setParameter(String string, String value);

	String getParameter(String string);

	boolean isReady();

	MID getID();

}
