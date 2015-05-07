package org.collabthings.environment;

import java.util.List;
import java.util.Set;

import org.collabthings.LLog;
import org.collabthings.LOTClient;
import org.collabthings.PrintOut;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTValues;

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

	PrintOut printOut();

	LOTEnvironment getEnvironment();

	void stop();

	void step(double dtime);

	boolean isRunning();

	Set<LOTRuntimeObject> getRunObjects();

	void addRunObject(String string, LOTRuntimeObject runo);

	LOTRuntimeObject getRunObject(String string);

	void recordEvent(LOTRuntimeObject runo, String string, LOTValues callvalues);

}
