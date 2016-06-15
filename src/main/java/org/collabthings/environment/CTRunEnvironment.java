package org.collabthings.environment;

import java.util.List;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import waazdoh.common.ObjectID;

public interface CTRunEnvironment {

	void setParameter(String string, ObjectID id);

	void setParameter(String string, String value);

	String getParameter(String s);

	LLog log();

	List<CTEnvironmentTask> getTasks();

	CTEnvironmentTask addTask(CTScriptRunner s, CTValues values);

	CTEnvironmentTask addTask(CTScriptRunner s);

	CTClient getClient();

	boolean isReady();

	void addListener(RunEnvironmentListener listener);

	String getInfo();

	PrintOut printOut();

	CTEnvironment getEnvironment();

	void stop();

	void step(double dtime);

	boolean isRunning();

	Set<CTRuntimeObject> getRunObjects();

	void addRunObject(String string, CTRuntimeObject runo);

	CTRuntimeObject getRunObject(String string);

	void recordEvent(CTRuntimeObject runo, String string, CTValues callvalues);

}
