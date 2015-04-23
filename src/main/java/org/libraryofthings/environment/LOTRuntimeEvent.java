package org.libraryofthings.environment;

import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTValues;

public class LOTRuntimeEvent {

	private LOTValues values;
	private String name;
	private LOTRuntimeObject object;

	public LOTRuntimeEvent(LOTRuntimeObject runo, String string, LOTValues callvalues) {
		this.setObject(runo);
		this.setName(string);
		this.setValues(callvalues);
	}

	public LOTRuntimeObject getObject() {
		return object;
	}

	public void setObject(LOTRuntimeObject object) {
		this.object = object;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LOTValues getValues() {
		return values;
	}

	public void setValues(LOTValues values) {
		this.values = values;
	}

}
