package org.libraryofthings.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.environment.LOTPartState;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.model.MID;
import waazdoh.util.MStringID;

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
	private boolean stopped;

	public LOTSimulationEnvironment(final LOTEnvironment nenv) {
		this.env = nenv;
	}

	public boolean step(double dtime) {
		for (LOTToolUser tooluser : toolusers) {
			tooluser.step(dtime);
		}
		return isRunning();
	}

	public boolean isRunning() {
		return !stopped;
	}

	@Override
	public void stop() {
		stopped = true;
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
		log.info("addTask " + s + " params: " + params);
		LOTTask task = new LOTTask(env, s, params);
		synchronized (tasks) {
			tasks.add(task);
		}
	}

	@Override
	public List<LOTTask> getTasks() {
		synchronized (tasks) {
			return new LinkedList<>(tasks);
		}
	}

	@Override
	public void removeTask(LOTTask task) {
		synchronized (tasks) {
			tasks.remove(task);
		}
	}

	@Override
	public LOTPartState addPart(String string, LOTSubPart part) {
		LOTPartState state = new LOTPartState(env, part);
		parts.put(string, state);
		return state;
	}

	@Override
	public LOTPartState getPart(String s) {
		return parts.get(s);
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

	public LVector getVector(double x, double y, double z) {
		return new LVector(x, y, z);
	}
}
