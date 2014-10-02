package org.libraryofthings.environment.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTScript;

public class LOTPool {
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

	public synchronized void waitForPart(String pool) {
		if (isPartPoolEmpty(pool)) {
			log.info("waiting for part " + pool);
		}

		while (isPartPoolEmpty(pool)) {
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
