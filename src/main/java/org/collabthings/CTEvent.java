package org.collabthings;

import java.util.LinkedList;
import java.util.List;

import org.collabthings.model.CTObject;

public class CTEvent {
	private List<CTObject> os = new LinkedList<>();
	private String info;

	public CTEvent(String string) {
		this.info = string;
	}

	public void addHandled(CTObject o) {
		os.add(o);
	}

	public boolean isHandled(CTObject o) {
		return os.contains(o);
	}
}
