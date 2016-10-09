package org.collabthings.environment.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.collabthings.environment.CTRuntimeEvent;

public class CTEvents {
	private List<CTRuntimeEvent> events = new ArrayList<>();

	public synchronized void add(CTRuntimeEvent e) {
		events.add(e);
	}

	public synchronized List<CTRuntimeEvent> getNewEvents(long after) {
		List<CTRuntimeEvent> list = new ArrayList<>();
		for (CTRuntimeEvent e : events) {
			if (e.getTime() > after) {
				list.add(e);
			}
		}

		Collections.sort(list, (CTRuntimeEvent o1, CTRuntimeEvent o2) -> (int) (o2.getTime() - o1.getTime()));

		return list;
	}
}
