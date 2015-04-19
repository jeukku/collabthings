package org.libraryofthings.model;

import java.util.Set;

import org.libraryofthings.PrintOut;
import org.libraryofthings.math.LVector;

import waazdoh.common.ObjectID;

public interface LOTEnvironment extends LOTObject {

	LOTScript getScript(String string);

	Set<String> getScripts();

	void addScript(String scriptname, LOTScript script);

	void renameScript(String oldname, String newname);

	void addTool(String string, LOTTool partsource);

	LOTTool getTool(String string);

	Set<String> getTools();

	void setParameter(String string, ObjectID id);

	void setParameter(String string, String value);

	String getParameter(String string);

	void setVectorParameter(String string, LVector v);

	LVector getVectorParameter(String name);

	boolean isReady();

	ObjectID getID();

	void deleteScript(String string);

	PrintOut printOut();

}
