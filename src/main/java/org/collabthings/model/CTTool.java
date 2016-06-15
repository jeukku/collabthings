package org.collabthings.model;

public interface CTTool extends CTObject {

	String getName();

	void setName(String string);

	CTScript getScript(String scriptname);

	CTScript addScript(String string);

	CTPart getPart();

	CTPart newPart();

}
