package org.libraryofthings.environment;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.model.LOTValues;

import waazdoh.util.ConditionWaiter;
import waazdoh.util.MStringID;

public class LOTFactoryState implements LOTRuntimeObject {
	final private LOTFactory factory;
	final private RunEnvironment runenv;
	//
	private LLog log;
	//
	final private String name;
	private LVector location = new LVector();

	private Set<LOTToolState> tools = new HashSet<LOTToolState>();
	private List<LOTToolUser> toolusers = new LinkedList<LOTToolUser>();
	private Set<LOTFactoryState> factories = new HashSet<>();

	private LOTPool pool = new LOTPool();
	private LOTRuntimeObject parent;

	private Set<LOTPartState> parts = new HashSet<>();

	public LOTFactoryState(final LOTClient client, LOTEnvironment env,
			final String name, final LOTFactory factory) {
		this.factory = factory;
		this.name = name;
		runenv = new LOTRunEnvironmentImpl(client, env);
		runenv.addRunObject("main", this);
		callStart();
	}

	public LOTFactoryState(final String name, final RunEnvironment runenv,
			final LOTFactory nfactory, final LOTFactoryState factorystate) {
		this.factory = nfactory;
		this.runenv = runenv;
		this.parent = factorystate;
		this.name = name;
		callStart();
	}

	private void callStart() {
		LOTValues values = new LOTValues("factory", factory);
		call("start", values);
	}

	private boolean isRunning() {
		return true;
	}

	public LVector getLocation() {
		return location;
	}

	public LVector getAbsoluteLocation() {
		return location;
	}

	@Override
	public void setParent(LOTRuntimeObject nfactorystate) {
		this.parent = nfactorystate;
	}

	public LOTPool getPool() {
		return pool;
	}

	@Override
	public String toString() {
		return "FactoryState[" + factory + "][" + location + "]";
	}

	public LOTFactoryState addFactory(String id, LOTFactory f) {
		getLog().info("addFactory " + id + " factory:" + f);
		LOTFactoryState state = new LOTFactoryState(id, runenv, f, this);
		factories.add(state);
		return state;
	}

	public LOTFactoryState getFactory(String id) {
		for (LOTFactoryState f : factories) {
			if (f.getName().equals(id)) {
				return f;
			}
		}

		return null;
	}

	private String getName() {
		return name;
	}

	public LOTToolState addTool(String id, LOTTool tool) {
		getLog().info("addTool " + id + " tool:" + tool);
		LOTToolState toolstate = new LOTToolState(id, runenv, tool, this);
		tools.add(toolstate);
		toolstate.setParent(this);
		return toolstate;
	}

	public LOTToolState getTool(String id) {
		LOTToolState tool = findTool(id);
		if (tool != null) {
			new ConditionWaiter(() -> !tool.isInUse() || !isRunning(), 0);
			return tool;
		} else {
			return null;
		}
	}

	private LOTToolState findTool(String id) {
		for (LOTToolState tool : tools) {
			if (tool.getName().equals(id) && !tool.isInUse()) {
				return tool;
			}
		}
		//
		for (LOTToolState tool : tools) {
			if (tool.getName().equals(id)) {
				return tool;
			}
		}

		return null;
	}

	private LLog getLog() {
		if (log == null) {
			log = LLog.getLogger(this);
		}
		return log;
	}

	public void addToolUser(LOTToolUser tooluser) {
		this.toolusers.add(tooluser);
		tooluser.setParent(this);
	}

	@Override
	public void step(double dtime) {
		for (LOTToolUser tooluser : toolusers) {
			tooluser.step(dtime);
		}
		//
		for (LOTToolState tool : tools) {
			tool.step(dtime);
		}
		//
		for (LOTFactoryState f : factories) {
			f.step(dtime);
		}
	}

	@Override
	public void stop() {
		for (LOTToolUser tooluser : toolusers) {
			tooluser.stop();
		}
		for (LOTFactoryState factory : factories) {
			factory.stop();
		}
	}

	public void requestMove(LOTToolState lotToolState, LVector l, LVector n) {
		LOTToolUser tooluser = getToolUser(lotToolState, l);
		getLog().info(
				"requestMove " + tooluser + " " + l + " tool:" + lotToolState);
		tooluser.setTool(lotToolState);
		tooluser.move(l, n);
	}

	public LOTToolUser getToolUser(final LOTToolState lotToolState, LVector l) {
		new ConditionWaiter(() -> !isRunning()
				|| getAvailableToolUser(lotToolState) != null, 0);
		return getAvailableToolUser(lotToolState);
	}

	private LOTToolUser getAvailableToolUser(LOTToolState toolstate) {
		for (LOTToolUser tooluser : toolusers) {
			if (tooluser.isAvailable(toolstate)) {
				return tooluser;
			}
		}
		return null;
	}

	public List<LOTToolUser> getToolUsers() {
		return new LinkedList<>(toolusers);
	}

	public RunEnvironment getRunEnvironment() {
		return this.runenv;
	}

	public LOTTask addTask(String task, LOTValues values) {
		return runenv.addTask(getScript(task), this, values);
	}

	public boolean call(String string) {
		return call(string, new LOTValues());
	}

	public boolean call(String string, LOTValues values) {
		LOTScript s = getScript(string);
		getLog().info("calling " + string + " " + s);
		return s.run(runenv, this, values);
	}

	public String getParameter(String name) {
		String param = this.runenv.getParameter(name);
		if (param == null) {
			param = this.factory.getEnvironment().getParameter(name);
		}
		if (param == null && parent != null) {
			param = parent.getParameter(name);
		}
		return param;
	}

	private LOTScript getScript(String string) {
		return factory.getScript(string);
	}

	public Set<LOTPartState> getParts() {
		return new HashSet<>(this.parts);
	}

	public LOTPart getPart(String s) {
		return runenv.getClient().getObjectFactory().getPart(new MStringID(s));
	}

	public LOTPartState newPart() {
		LOTPartState partstate = new LOTPartState(runenv, runenv.getClient()
				.getObjectFactory().getPart());
		parts.add(partstate);
		return partstate;
	}

	public LOTFactory getFactory() {
		return factory;
	}

	public List<LOTFactoryState> getFactories() {
		return new LinkedList<LOTFactoryState>(this.factories);
	}

	// TODO FIXME
	/*
	 * Will be removed in future version
	 */
	public void addSuperheroRobot() {
		addToolUser(new ReallySimpleSuperheroRobot(runenv));
	}
}
