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
import org.collabthings.util.LLog;

import waazdoh.client.utils.ConditionWaiter;

public final class CTTaskImpl implements CTEnvironmentTask {
	private CTApplicationRunner s;
	private CTRunEnvironment values;
	private boolean isrun;

	public CTTaskImpl(CTApplicationRunner s2, CTRunEnvironment values2) {
		this.s = s2;
		this.values = values2;
		//
		LLog.getLogger(this).info("CTTask " + s.toString());
	}

	@Override
	public boolean run() {
		boolean ret = s.run(values);
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
		return "CTTask[" + this.s + "][" + values + "]";
	}

	public boolean isRun() {
		return isrun;
	}
}
