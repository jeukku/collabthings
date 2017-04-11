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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTScript;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

public class CTPool {
	private static final int MAX_WAIT_TIME = 100000;
	private final Map<String, List<CTPart>> parts = new HashMap<>();
	private final Map<CTScript, CTScriptRunnerImpl> scriptrunners = new HashMap<>();
	private final CTRuntimeObject runtimeobject;
	private final CTRunEnvironment runenv;

	private LLog log;

	public CTPool(CTRunEnvironment nrunenv, CTRuntimeObject nruntimeobject) {
		this.runenv = nrunenv;
		this.runtimeobject = nruntimeobject;
		log = LLog.getLogger(this);
	}

	@Override
	public String toString() {
		return "Pool[" + runtimeobject + "]";
	}

	public CTPart peekPart(String string) {
		List<CTPart> list = parts.get(string);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public synchronized CTPart getPart(String string) {
		try {
			List<CTPart> list = parts.get(string);
			if (list != null && !list.isEmpty()) {
				return list.remove(0);
			} else {
				return null;
			}
		} finally {
			this.notifyAll();
		}
	}

	public synchronized void addPart(String pool, CTPart p) {
		log.info("Adding part " + p + " to pool " + pool);
		List<CTPart> list = parts.get(pool);
		if (list == null) {
			list = new ArrayList<>();
			parts.put(pool, list);
		}
		list.add(p);
		//
		this.notifyAll();
	}

	public void waitForPart(String string) {
		waitForPart(string, CTPool.MAX_WAIT_TIME);
	}

	public synchronized void waitForPart(String pool, long maxtime) {
		long st = System.currentTimeMillis();

		if (isPartPoolEmpty(pool)) {
			log.info("waiting for part " + pool);
		}

		try {
			while (isPartPoolEmpty(pool) && (System.currentTimeMillis() - st) < maxtime) {
				this.wait(maxtime / 4);
				log.info("waiting for part " + pool + " content:" + this.parts);
			}
		} catch (InterruptedException e) {
			log.error(this, "waitForPart", e);
			Thread.currentThread().interrupt();
		}

		log.info("Waiting for pool " + pool + " done");
	}

	public boolean isPartPoolEmpty(String pool) {
		return parts.get(pool) == null || parts.get(pool).isEmpty();
	}

	public int countParts(String pool) {
		List<CTPart> list = parts.get(pool);
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
	}

	public PrintOut printOut() {
		PrintOut p = new PrintOut();
		p.append("" + this);
		p.append(PrintOut.INDENT, "parts");
		for (String pname : parts.keySet()) {
			p.append(PrintOut.INDENT2, "count:" + countParts(pname));
		}

		return p;
	}

	public CTScriptRunnerImpl getScript(CTScript script) {
		CTScriptRunnerImpl runner = scriptrunners.get(script);
		if (runner == null && script != null) {
			runner = new CTScriptRunnerImpl(script, this.runenv, this.runtimeobject);
			scriptrunners.put(script, runner);
		}
		return runner;
	}
}
