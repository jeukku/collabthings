package org.libraryofthings.environment;

import java.util.List;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTValues;

import waazdoh.common.ObjectID;

public interface LOTRunEnvironment {

	void setParameter(String string, ObjectID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	LLog log();

	List<LOTTask> getTasks();

	LOTTask addTask(LOTScriptRunner s, LOTValues values);

	LOTTask addTask(LOTScriptRunner s);

	LOTClient getClient();

	boolean isReady();

	void addListener(RunEnvironmentListener listener);

	String getInfo();

	LOTEnvironment getEnvironment();

	void stop();

	void step(double dtime);

	boolean isRunning();

	Set<LOTRuntimeObject> getRunObjects();

	void addRunObject(String string, LOTRuntimeObject runo);

}
