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

package org.collabthings.environment.impl;

import org.collabthings.application.CTApplicationRunner;
import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;

import collabthings.core.utils.ConditionWaiter;

public final class CTTaskImpl implements CTEnvironmentTask {
	private CTApplicationRunner s;
	private CTRunEnvironment runenv;
	private boolean isrun;
	private CTValues values;

	public CTTaskImpl(CTApplicationRunner s2, CTRunEnvironment runenv, CTValues values) {
		this.s = s2;
		this.runenv = runenv;
		this.values = values;
		//
		LLog.getLogger(this).info("CTTask " + s.toString());
	}

	@Override
	public boolean run() {
		boolean ret = s.run(runenv, values);
		isrun = true;
		LLog.getLogger(this).info("Task done " + ret);
		return ret;
	}

	@Override
	public String getError() {
		return s.getError();
	}

	public void waitUntilFinished() {
		ConditionWaiter.wait(() -> this.isrun, 0);
	}

	@Override
	public String toString() {
		return "CTTask[" + this.s + "][" + runenv + "]";
	}

	public boolean isRun() {
		return isrun;
	}
}
