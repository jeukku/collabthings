package org.libraryofthings.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.environment.LOTPartState;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.cutils.MID;
import waazdoh.cutils.MStringID;

public class LOTSimulationEnvironment implements RunEnvironment {
	private Map<String, LOTPartState> parts = new HashMap<String, LOTPartState>();
	private Map<String, LOTScript> scripts = new HashMap<String, LOTScript>();
	private Map<String, LOTToolState> tools = new HashMap<String, LOTToolState>();
	private Map<String, String> params = new HashMap<String, String>();
	private List<LOTTask> tasks = new LinkedList<LOTTask>();
	private List<LOTToolUser> toolusers = new LinkedList<LOTToolUser>();

	private LOTEnvironment env;
	private LLog log = LLog.getLogger(this);
	private LOTPartState basepart;

	public LOTSimulationEnvironment(final LOTEnvironment nenv) {
		this.env = nenv;
	}

	@Override
	public LOTPartState getBasePart() {
		if (basepart == null) {
			basepart = new LOTPartState(env, env.getObjectFactory().getPart()
					.newSubPart());
		}
		return basepart;
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
	public void addTask(final LOTScript s, final Object... params) {
		LOTTask task = new LOTTask(env, s, params);
		tasks.add(task);
	}

	@Override
	public List<LOTTask> getTasks() {
		return new LinkedList<>(tasks);
	}

	@Override
	public LOTPartState addPart(String string, LOTSubPart part) {
		LOTPartState state = new LOTPartState(env, part);
		parts.put(string, state);
		return state;
	}

	@Override
	public LOTPartState getPart(String s) {
		LOTPartState p = parts.get(s);
		return p;
	}

	@Override
	public LOTPart getOriginalPart(String s) {
		return env.getObjectFactory().getPart(new MStringID(s));
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
		LOTToolState toolstate = new LOTToolState(this, tool);
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

	@Override
	public void addToolUser(LOTToolUser tooluser) {
		this.toolusers.add(tooluser);
	}

	@Override
	public void requestMove(LOTToolState lotToolState, LVector l, LVector n) {
		LOTToolUser tooluser = getToolUser(lotToolState, l);
		tooluser.setTool(lotToolState);
		tooluser.move(l, n);
	}

	private LOTToolUser getToolUser(LOTToolState lotToolState, LVector l) {
		for (LOTToolUser tooluser : toolusers) {
			return tooluser;
		}
		return null;
	}

	public boolean isRunning() {
		return tasks.size() > 0;
	}
}
