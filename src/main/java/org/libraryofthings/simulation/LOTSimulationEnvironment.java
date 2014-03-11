package org.libraryofthings.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.LOTToolState;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;

import waazdoh.cutils.MID;
import waazdoh.cutils.MStringID;

public class LOTSimulationEnvironment implements RunEnvironment {
	private Map<String, LOTPart> parts = new HashMap<String, LOTPart>();
	private Map<String, LOTScript> scripts = new HashMap<String, LOTScript>();
	private Map<String, LOTToolState> tools = new HashMap<String, LOTToolState>();
	private Map<String, String> params = new HashMap<String, String>();
	private List<LOTSimulationTask> tasks = new LinkedList<LOTSimulationTask>();

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

	public void addTask(LOTScript s, Object... params) {
		LOTSimulationTask task = new LOTSimulationTask(env, s, params);
		tasks.add(task);
	}

	@Override
	public List<LOTSimulationTask> getTasks() {
		return new LinkedList<>(tasks);
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

	@Override
	public void addScript(String string, LOTScript script) {
		scripts.put(string, script);
	}

	public LOTScript getScript(String name) {
		return scripts.get(name);
	}

	@Override
	public LOTToolState addTool(String id, LOTTool tool) {
		LOTToolState toolstate = new LOTToolState(env, tool);
		tools.put(id, toolstate);
		return toolstate;
	}

	@Override
	public LOTToolState getTool(String id) {
		LOTToolState tool = tools.get(id);
		if (tool != null) {
			return tool;
		} else {
			MStringID stringid = new MStringID(id);
			return addTool(id, env.getObjectFactory().getTool(stringid));
		}
	}
}
