package org.libraryofthings.environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.model.LOTPart;

public class LOTPool {
	private Map<String, List<LOTPart>> parts = new HashMap<>();

	public LOTPart getPart(String string) {
		List<LOTPart> list = parts.get(string);
		if (list != null && !list.isEmpty()) {
			return list.remove(0);
		} else {
			return null;
		}
	}

	public void addPart(String pool, LOTPart p) {
		List<LOTPart> list = parts.get(pool);
		if (list == null) {
			list = new LinkedList<LOTPart>();
			parts.put(pool, list);
		}
		list.add(p);
	}
}
