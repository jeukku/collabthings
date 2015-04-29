package org.collabthings.environment.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.environment.LOTRuntimeEvent;

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

		Collections.sort(list, new Comparator<LOTRuntimeEvent>() {
			@Override
			public int compare(LOTRuntimeEvent o1, LOTRuntimeEvent o2) {
				return (int) (o2.getTime() - o1.getTime());
			}
		});

		return list;
	}
}
