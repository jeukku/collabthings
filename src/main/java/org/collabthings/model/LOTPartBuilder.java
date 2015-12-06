package org.collabthings.model;

public interface LOTPartBuilder extends LOTObject {

	void setScript(LOTScript s);

	boolean run(LOTPart p);

	String getError();

	String getName();

	void setName(String string);

	LOTScript getScript();

}
