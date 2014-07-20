package org.libraryofthings.environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.model.LOTPart;

public class LOTPool {
	private Map<String, List<LOTPart>> parts = new HashMap<>();
	private LLog log = LLog.getLogger(this);

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
	}

	private boolean isPartPoolEmpty(String pool) {
		return parts.get(pool) == null || parts.get(pool).isEmpty();
	}

	private synchronized void waitABit() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			LLog.getLogger(this).error(this, "wait", e);
		}
	}
}
