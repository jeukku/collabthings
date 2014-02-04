package org.libraryofthings;

import java.util.HashMap;
import java.util.Map;

import org.libraryofthings.model.LOTPart;

import waazdoh.cutils.MID;

public class LOTSimulationEnvironment implements RunEnvironment {
	private Map<String, LOTPart> parts = new HashMap<String, LOTPart>();
	private Map<String, String> params = new HashMap<String, String>();

	@Override
	public void setParameter(String key, MID id) {
		setParameter(key, id.getStringID().toString());
	}

	@Override
	public void setParameter(String key, String value) {
		params.put(key, value);
	}

	@Override
	public void addPart(String string, LOTPart part) {
		parts.put(string, part);
	}

	public String getParameter(String string) {
		return params.get(string);
	}

}
