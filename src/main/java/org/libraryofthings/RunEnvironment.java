package org.libraryofthings;

import org.libraryofthings.model.LOTPart;

import waazdoh.cutils.MID;

public interface RunEnvironment {

	void setParameter(String string, MID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	void addPart(String string, LOTPart part);

	LOTPart getPart(String s);

	LLog log();
}
