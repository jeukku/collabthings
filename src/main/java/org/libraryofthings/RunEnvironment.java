package org.libraryofthings;

import java.util.List;

import org.libraryofthings.environment.LOTPartState;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.cutils.MID;

public interface RunEnvironment {

	void setParameter(String string, MID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	LOTPartState addPart(String string, LOTSubPart part);

	LOTPartState getPart(String s);

	LLog log();

	LOTToolState addTool(String id, LOTTool tool);

	LOTToolState getTool(String id);

	void addScript(String string, LOTScript loadScript);

	List<LOTTask> getTasks();

	void addTask(LOTScript s, Object ... params);

	LOTPart getOriginalPart(String s);

	LOTPartState getBasePart();

	void addToolUser(LOTToolUser tooluser);

	void requestMove(LOTToolState lotToolState, LVector l, LVector n);

	boolean isRunning();

}
