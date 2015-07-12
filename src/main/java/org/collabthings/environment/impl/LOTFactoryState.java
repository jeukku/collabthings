package org.collabthings.environment.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.LOTTask;
import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTAttachedFactory;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTRuntimeObject;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import waazdoh.client.utils.ConditionWaiter;
import waazdoh.common.MStringID;

public class LOTFactoryState implements LOTRuntimeObject {
	private static final long DEFAULT_WAIT = 100;

	private final LOTAttachedFactory factory;
	private final LOTRunEnvironment runenv;
	private LLog log;
	private final String name;

	private Set<LOTToolState> tools = new HashSet<LOTToolState>();
	private List<LOTToolUser> toolusers = new LinkedList<LOTToolUser>();
	private List<LOTFactoryState> factories = new LinkedList<>();
	private Set<LOTRuntimeStepper> steppers = new HashSet<>();
	private Map<String, String> params = new HashMap<>();
	private Set<LOTPartState> parts = new HashSet<>();

	private final LOTPool pool;
	private LOTRuntimeObject parent;
	private LOTEvents events = new LOTEvents();
	private LOrientation orientation;

	public LOTFactoryState(final LOTClient client, LOTEnvironment env,
			final String name, final LOTAttachedFactory factory) {
		this.factory = factory;
		this.name = name;
		runenv = new LOTRunEnvironmentImpl(client, env);
		runenv.addRunObject("main", this);
		pool = new LOTPool(runenv, this);
		init();
	}

	public LOTFactoryState(final String name, final LOTRunEnvironment runenv,
			final LOTAttachedFactory nfactory,
			final LOTFactoryState factorystate) {
		this.factory = nfactory;
		this.runenv = runenv;
		this.parent = factorystate;
		this.name = name;
		pool = new LOTPool(this.runenv, this);
		init();
	}

	public LOTFactoryState(LOTClient client, LOTEnvironment env, String name2,
			LOTFactory factory) {
		this(client, env, name2, new LOTAttachedFactory(factory));
	}

	@Override
	public PrintOut printOut() {
		PrintOut p = new PrintOut();
		p.append("factorystate");
		p.append(PrintOut.INDENT, "factory " + factory);
		p.append(PrintOut.INDENT, "transformation " + getTransformation());

		p.append(PrintOut.INDENT, "factories");
		for (LOTFactoryState fs : factories) {
			p.append(PrintOut.INDENT2, fs.printOut());
		}

		p.append(PrintOut.INDENT, "parts");
		for (LOTPartState ps : parts) {
			p.append(PrintOut.INDENT2, ps.printOut());
		}

		p.append(PrintOut.INDENT, "toolusers");
		for (LOTToolUser tu : toolusers) {
			p.append(PrintOut.INDENT2, tu.printOut());
		}

		return p;
	}

	private void init() {
		orientation = factory.getOrientation();

		for (String fname : getFactory().getFactories()) {
			LOTAttachedFactory f = getFactory().getFactory(fname);
			addFactory(fname, f);
		}

		LOTValues values = new LOTValues("factory", getFactory());
		call("start", values);
	}

	private boolean isRunning() {
		return true;
	}

	public LOrientation getOrientation() {
		return orientation;
	}

	public LVector getTransformedVector(final LVector l) {
		LVector c = l.copy();
		getTransformation().transform(c);
		return c;
	}

	public LTransformation getTransformation() {
		return new LTransformation(orientation);
	}

	public LOTPool getPool() {
		return pool;
	}

	@Override
	public String toString() {
		return "FS[" + getFactory() + "]";
	}

	private LOTFactoryState addFactory(String id, LOTAttachedFactory f) {
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

		log.info("returing null factory with id " + id);

		return null;
	}

	public String getName() {
		return name;
	}

	public LOTToolState addTool(String id, LOTTool tool) {
		getLog().info("addTool " + id + " tool:" + tool);
		LOTToolState toolstate = new LOTToolState(id, runenv, tool, this);
		tools.add(toolstate);
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
	}

	@Override
	public void step(double dtime) {
		stepToolUsers(dtime);
		stepTools(dtime);
		stepFactories(dtime);
		stepSteppers(dtime);
	}

	private void stepSteppers(double dtime) {
		synchronized (steppers) {
			for (LOTRuntimeStepper stepper : new HashSet<>(steppers)) {
				if (stepper.step(dtime)) {
					steppers.remove(stepper);
				}
			}
		}
	}

	private void stepFactories(double dtime) {
		synchronized (factories) {
			for (LOTFactoryState f : factories) {
				f.step(dtime);
			}
		}
	}

	private void stepTools(double dtime) {
		synchronized (tools) {
			for (LOTToolState tool : tools) {
				tool.step(dtime);
			}
		}
	}

	private void stepToolUsers(double dtime) {
		synchronized (toolusers) {
			for (LOTToolUser tooluser : toolusers) {
				tooluser.step(dtime);
			}
		}
	}

	@Override
	public void stop() {
		for (LOTToolUser tooluser : toolusers) {
			tooluser.stop();
		}
		for (LOTFactoryState cfactory : factories) {
			cfactory.stop();
		}
	}

	public void requestMove(LOTToolState lotToolState, LVector l, LVector n,
			double angle) {
		LOTToolUser tooluser = getToolUser(lotToolState, l);
		getLog().info(
				"requestMove " + tooluser + " " + l + " tool:" + lotToolState);
		tooluser.setTool(lotToolState);
		tooluser.move(l, n, angle);
	}

	public LOTToolUser getToolUser(final LOTToolState lotToolState, LVector l) {
		new ConditionWaiter(() -> !isRunning()
				|| getAvailableToolUser(lotToolState) != null, 0);
		return getAvailableToolUser(lotToolState, l);
	}

	private LOTToolUser getAvailableToolUser(LOTToolState toolstate) {
		for (LOTToolUser tooluser : toolusers) {
			if (tooluser.isAvailable(toolstate)) {
				return tooluser;
			}
		}
		return null;
	}

	private LOTToolUser getAvailableToolUser(LOTToolState toolstate, LVector l) {
		LinkedList<LOTToolUser> ts = new LinkedList<LOTToolUser>(toolusers);
		ts.sort(new Comparator<LOTToolUser>() {
			@Override
			public int compare(LOTToolUser a, LOTToolUser b) {
				double adistance = a.getOrientation().getLocation().getSub(l)
						.length();
				double bdistance = b.getOrientation().getLocation().getSub(l)
						.length();
				if (adistance < bdistance) {
					return -1;
				} else if (adistance > bdistance) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		for (LOTToolUser tooluser : ts) {
			if (tooluser.isAvailable(toolstate)) {
				return tooluser;
			}
		}
		return null;
	}

	public List<LOTToolUser> getToolUsers() {
		return new LinkedList<>(toolusers);
	}

	public LOTRunEnvironment getRunEnvironment() {
		return this.runenv;
	}

	public LOTTask addTask(String task, LOTValues values) {
		return runenv.addTask(getScript(task), values);
	}

	public boolean call(String string) {
		return call(string, new LOTValues());
	}

	public boolean call(String string, LOTValues values) {
		LOTScriptRunnerImpl s = getScript(string);
		getLog().info("calling " + string + " " + s);
		if (s != null) {
			addEvent(string, values);
			return s.run(values);
		} else {
			getLog().info("Script \"" + string + "\" doesn't exist");
			getLog().info(runenv.printOut().toText());
			getLog().info(getFactory().printOut().toText());
			return false;
		}
	}

	public void setStateParameter(String name, String value) {
		params.put(name, value);
	}

	public String getStateParameter(String name) {
		return params.get(name);
	}

	public String getParameter(String name) {
		String param = this.runenv.getParameter(name);
		if (param == null) {
			param = this.getFactory().getEnvironment().getParameter(name);
		}
		if (param == null && parent != null) {
			param = parent.getParameter(name);
		}
		return param;
	}

	private LOTScriptRunnerImpl getScript(String string) {
		LOTScript script = runenv.getEnvironment().getScript(string);
		if (script == null) {
			script = getFactory().getScript(string);
		}

		return pool.getScript(script);
	}

	public LOTFactory getFactory() {
		return factory.getFactory();
	}

	public Set<LOTPartState> getParts() {
		synchronized (parts) {
			return new HashSet<>(this.parts);
		}
	}

	public LOTPart getPart(String s) {
		return runenv.getClient().getObjectFactory().getPart(new MStringID(s));
	}

	public LOTPartState newPart() {
		LOTPartState partstate = new LOTPartState(runenv, this, runenv
				.getClient().getObjectFactory().getPart());
		addPart(partstate);
		return partstate;
	}

	private void addPart(LOTPartState partstate) {
		synchronized (parts) {
			parts.add(partstate);
		}
	}

	public void remove(LOTPartState partstate) {
		synchronized (parts) {
			parts.remove(partstate);
		}
	}

	public List<LOTFactoryState> getFactories() {
		return new LinkedList<LOTFactoryState>(this.factories);
	}

	public LVector getVector(String name) {
		return getFactory().getEnvironment().getVectorParameter(name);
	}

	// TODO FIXME
	/**
	 * Will be removed in future version.
	 */
	public void addSuperheroRobot() {
		ReallySimpleSuperheroRobot tooluser = new ReallySimpleSuperheroRobot(
				runenv, this, getFactory().getToolUserSpawnLocation());
		addToolUser(tooluser);
	}

	public void stepWhile(LOTRuntimeStepper r) {
		log.info("stepWhile " + r);
		addStepper(r);
		waitStepperDone(r);
	}

	private void waitStepperDone(LOTRuntimeStepper r) {
		try {
			while (!isStepperDone(r)) {
				synchronized (steppers) {
					steppers.wait(DEFAULT_WAIT);
				}
			}
		} catch (InterruptedException e) {
			log.error(this, "waitStepperDone", e);
		}
	}

	private boolean isStepperDone(LOTRuntimeStepper r) {
		synchronized (steppers) {
			for (LOTRuntimeStepper stepper : steppers) {
				if (stepper == r) {
					return false;
				}
			}
		}
		return true;
	}

	private void addStepper(LOTRuntimeStepper s) {
		synchronized (steppers) {
			steppers.add(s);
		}
	}

	private void addEvent(String event, LOTValues callvalues) {
		LOTRuntimeEvent e = new LOTRuntimeEvent(this, event, callvalues);
		events.add(e);
	}

	public LOTEvents getEvents() {
		return events;
	}
}
