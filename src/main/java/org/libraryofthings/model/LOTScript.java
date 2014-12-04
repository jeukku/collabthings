package org.libraryofthings.model;

import javax.script.Invocable;

public interface LOTScript extends LOTObject {

	boolean isOK();

	String getScript();

	boolean setScript(String string);

	void setName(String scriptname);

	Invocable getInvocable();

	String getError();

}
