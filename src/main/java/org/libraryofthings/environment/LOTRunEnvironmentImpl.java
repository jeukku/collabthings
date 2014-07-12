package org.libraryofthings.environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.model.MID;
import waazdoh.util.ConditionWaiter;
import waazdoh.util.MStringID;

public class LOTRunEnvironmentImpl implements RunEnvironment {
	private Map<String, LOTPartState> parts = new HashMap<String, LOTPartState>();
	private Map<String, LOTScript> scripts = new HashMap<String, LOTScript>();
	private Map<String, LOTToolState> tools = new HashMap<String, LOTToolState>();
	private Map<String, String> params = new HashMap<String, String>();
	private List<LOTTask> tasks = new LinkedList<LOTTask>();
	private List<LOTToolUser> toolusers = new LinkedList<LOTToolUser>();
	private LOTPool pool = new LOTPool();

	private LOTClient env;
	private RunEnvironment parent;
	//
	private LLog log = LLog.getLogger(this);
	private LOTPartState basepart;
	private boolean stopped;

	public LOTRunEnvironmentImpl(final LOTClient nenv) {
		this.env = nenv;
	}

	public LOTRunEnvironmentImpl(RunEnvironment runenv) {
		this.parent = runenv;
		this.env = runenv.getClient();
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
		log.info("Stop");
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
	public RunEnvironment getParent() {
		return parent;
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
	public LOTTask addTask(final LOTScript s, final Object... params) {
		log.info("addTask " + s + " params: " + params);
		LOTTask task = new LOTTask(env, s, params);
		synchronized (tasks) {
			tasks.add(task);
		}
		return task;
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
		//
		if (tool != null) {
			new ConditionWaiter(() -> !tool.isInUse() || !isRunning(), 0);
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

	@Override
	public LOTClient getClient() {
		return env;
	}

	@Override
	public LOTPool getPool() {
		return pool;
	}
}
