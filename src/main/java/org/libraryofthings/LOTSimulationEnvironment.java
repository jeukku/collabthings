package org.libraryofthings;

import java.util.HashMap;
import java.util.Map;

import org.libraryofthings.model.LOTPart;

import waazdoh.cutils.MID;
import waazdoh.cutils.MStringID;

public class LOTSimulationEnvironment implements RunEnvironment {
	private Map<String, LOTPart> parts = new HashMap<String, LOTPart>();
	private Map<String, String> params = new HashMap<String, String>();
	private LOTEnvironment env;
	private LLog log = LLog.getLogger(this);

	public LOTSimulationEnvironment(final LOTEnvironment nenv) {
		this.env = nenv;
	}

	@Override
	public LLog log() {
		return log;
	}

	@Override
	public void setParameter(String key, MID id) {
		setParameter(key, id.getStringID().toString());
	}

	@Override
	public String getParameter(String string) {
		return params.get(string);
	}

	@Override
	public void setParameter(String key, String value) {
		params.put(key, value);
	}

	@Override
	public void addPart(String string, LOTPart part) {
		parts.put(string, part);
	}

	@Override
	public LOTPart getPart(String s) {
		LOTPart p = parts.get(s);
		if (p != null) {
			return p;
		} else {
			return env.getObjectFactory().getPart(new MStringID(s));
		}
	}

}
