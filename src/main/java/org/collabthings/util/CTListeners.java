package org.collabthings.util;

import java.util.ArrayList;
import java.util.List;

import org.collabthings.CTListener;

public class CTListeners {
	private List<CTListener> listeners = new ArrayList<>(0);

	public void add(CTListener l) {
		this.listeners.add(l);
	}

	public void fireEvent() {
		listeners.stream().forEach((l) -> l.event());
	}

}
