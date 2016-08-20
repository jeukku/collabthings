package org.collabthings.environment.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.collabthings.environment.CTRuntimeEvent;

public class CTEvents {
	private List<CTRuntimeEvent> events = new ArrayList<CTRuntimeEvent>();

	public synchronized void add(CTRuntimeEvent e) {
		events.add(e);
	}

	public synchronized List<CTRuntimeEvent> getNewEvents(long after) {
		List<CTRuntimeEvent> list = new ArrayList<CTRuntimeEvent>();
		for (CTRuntimeEvent e : events) {
			if (e.getTime() > after) {
				list.add(e);
			}
		}

		Collections.sort(list, new Comparator<CTRuntimeEvent>() {
			@Override
			public int compare(CTRuntimeEvent o1, CTRuntimeEvent o2) {
				return (int) (o2.getTime() - o1.getTime());
			}
		});

		return list;
	}
}
