package org.collabthings.environment;

import java.util.List;
import java.util.Set;

import org.collabthings.LOTClient;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import waazdoh.common.ObjectID;

public interface LOTRunEnvironment {

	void setParameter(String string, ObjectID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	LLog log();

	List<LOTEnvironmentTask> getTasks();

	LOTEnvironmentTask addTask(LOTScriptRunner s, LOTValues values);

	LOTEnvironmentTask addTask(LOTScriptRunner s);

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
