package org.collabthings.model;

public interface LOTTool extends LOTObject {

	String getName();

	void setName(String string);

	LOTScript getScript(String scriptname);

	LOTScript addScript(String string);

	LOTPart getPart();

	LOTPart newPart();

}
