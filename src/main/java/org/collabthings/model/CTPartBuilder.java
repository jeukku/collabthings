package org.collabthings.model;

public interface CTPartBuilder extends CTObject {

	void setScript(CTScript s);

	boolean run(CTPart p);

	String getError();

	String getName();

	void setName(String string);

	CTScript getScript();

}
