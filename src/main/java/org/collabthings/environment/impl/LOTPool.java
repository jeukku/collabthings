package org.collabthings.environment.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.collabthings.LLog;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTScript;

public class LOTPool {
	private static final int MAX_WAIT_TIME = 100000;
	final private Map<String, List<LOTPart>> parts = new HashMap<>();
	final private Map<LOTScript, LOTScriptRunnerImpl> scriptrunners = new HashMap<>();
	final private LOTRuntimeObject runtimeobject;
	final private LOTRunEnvironment runenv;

	private LLog log;

	public LOTPool(LOTRunEnvironment nrunenv, LOTRuntimeObject nruntimeobject) {
		this.runenv = nrunenv;
		this.runtimeobject = nruntimeobject;
		log = LLog.getLogger(this);
	}

	@Override
	public String toString() {
		return "Pool[" + runtimeobject + "]";
	}

	public LOTPart peekPart(String string) {
		List<LOTPart> list = parts.get(string);
		if (list != null && !list.isEmpty() ) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public synchronized LOTPart getPart(String string) {
		try {
			List<LOTPart> list = parts.get(string);
			if (list != null && !list.isEmpty()) {
				return list.remove(0);
			} else {
				return null;
			}
		} finally {
			this.notifyAll();
		}
	}

	public synchronized void addPart(String pool, LOTPart p) {
		log.info("Adding part " + p + " to pool " + pool);
		List<LOTPart> list = parts.get(pool);
		if (list == null) {
			list = new LinkedList<LOTPart>();
			parts.put(pool, list);
		}
		list.add(p);
		//
		this.notifyAll();
	}

	public void waitForPart(String string) {
		waitForPart(string, LOTPool.MAX_WAIT_TIME);
	}

	public synchronized void waitForPart(String pool, int maxtime) {
		long st = System.currentTimeMillis();

		if (isPartPoolEmpty(pool)) {
			log.info("waiting for part " + pool);
		}

		while (isPartPoolEmpty(pool)
				&& (System.currentTimeMillis() - st) < maxtime) {
			waitABit();
		}

		log.info("Waiting for pool " + pool + " done");
	}

	public boolean isPartPoolEmpty(String pool) {
		return parts.get(pool) == null || parts.get(pool).isEmpty();
	}

	private synchronized void waitABit() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			LLog.getLogger(this).error(this, "wait", e);
		}
	}

	public LOTScriptRunnerImpl getScript(LOTScript script) {
		LOTScriptRunnerImpl runner = scriptrunners.get(script);
		if (runner == null && script != null) {
			runner = new LOTScriptRunnerImpl(script, this.runenv,
					this.runtimeobject);
			scriptrunners.put(script, runner);
		}
		return runner;
	}
}
