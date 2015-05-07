package org.collabthings.model;

import javax.script.Invocable;

public interface LOTScript extends LOTObject {

	boolean isOK();

	String getScript();

	void setScript(String string);

	void setName(String scriptname);

	Invocable getInvocable();

	String getError();

	String getName();

}
