package org.collabthings.model;

public interface LOTPartBuilder extends LOTObject {

	void setScript(LOTScript s);

	boolean run(LOTPart p);

	String getError();

}
