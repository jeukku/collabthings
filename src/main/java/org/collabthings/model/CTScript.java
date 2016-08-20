package org.collabthings.model;

import javax.script.Invocable;

public interface CTScript extends CTObject {

	boolean isOK();

	String getScript();

	void setScript(String string);

	void setName(String scriptname);

	Invocable getInvocable();

	String getError();

	String getName();

	String getInfo();

}
