/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.environment;

import java.util.List;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import waazdoh.common.WObjectID;

public interface CTRunEnvironment {

	void setParameter(String string, WObjectID id);

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
