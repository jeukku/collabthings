package org.libraryofthings.environment;

import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.model.MID;

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

	LOTTask addTask(LOTScript s, Object... params);

	void removeTask(LOTTask task);

	LOTPart getOriginalPart(String s);

	LOTPartState getBasePart();

	void addToolUser(LOTToolUser tooluser);

	void requestMove(LOTToolState lotToolState, LVector l, LVector n);

	boolean isRunning();

	boolean step(double dtime);

	void stop();

	LOTClient getClient();

	RunEnvironment getParent();

	LOTPool getPool();

}
