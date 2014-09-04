package org.libraryofthings.environment;

import java.util.List;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTValues;

import waazdoh.client.model.MID;

public interface RunEnvironment {

	void setParameter(String string, MID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	LLog log();

	List<LOTTask> getTasks();

	LOTTask addTask(LOTScript s, LOTRuntimeObject runtimeobject,
			LOTValues values);

	LOTTask addTask(LOTScript script);

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
