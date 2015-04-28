package org.libraryofthings.environment.impl;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.environment.LOTRuntimeEvent;

public class LOTEvents {
	private List<LOTRuntimeEvent> events = new LinkedList<LOTRuntimeEvent>();

	public void add(LOTRuntimeEvent e) {
		events.add(e);
	}

	public List<LOTRuntimeEvent> getNewEvents(long after) {
		List<LOTRuntimeEvent> list = new LinkedList<LOTRuntimeEvent>();
		for (LOTRuntimeEvent e : events) {
			if (e.getTime() > after) {
				list.add(e);
			}
		}
		return list;
	}
}
