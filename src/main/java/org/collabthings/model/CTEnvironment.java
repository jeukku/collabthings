package org.collabthings.model;

import java.util.Set;

import org.collabthings.math.LVector;
import org.collabthings.util.PrintOut;

import waazdoh.common.ObjectID;

public interface CTEnvironment extends CTObject {

	CTScript getScript(String string);

	Set<String> getScripts();

	void addScript(String scriptname, CTScript script);

	void renameScript(String oldname, String newname);

	void addTool(String string, CTTool partsource);

	CTTool getTool(String string);

	void renameTool(String string, String text);

	Set<String> getTools();

	void deleteTool(String string);

	Set<String> getParameters();

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
