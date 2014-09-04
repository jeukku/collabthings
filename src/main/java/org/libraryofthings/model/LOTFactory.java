package org.libraryofthings.model;

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

	void setLocation(LVector lVector);

}
