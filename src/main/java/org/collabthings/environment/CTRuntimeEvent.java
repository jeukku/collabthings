package org.collabthings.environment;

import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTValues;

public class CTRuntimeEvent {

	private CTValues values;
	private String name;
	private CTRuntimeObject object;
	private long time = System.currentTimeMillis();

	public CTRuntimeEvent(CTRuntimeObject runo, String string, CTValues callvalues) {
		this.setObject(runo);
		this.setName(string);
		this.setValues(callvalues);
	}

	public CTRuntimeObject getObject() {
		return object;
	}

	public long getTime() {
		return time;
	}

	public void setObject(CTRuntimeObject object) {
		this.object = object;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CTValues getValues() {
		return values;
	}

	public void setValues(CTValues values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "" + getName() + "[" + getTime() + "] " + getObject() + " with " + values;
	}
}