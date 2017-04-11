/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.environment.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.environment.CTEnvironmentTask;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import waazdoh.client.utils.ConditionWaiter;
import waazdoh.common.WStringID;

public class CTFactoryState implements CTRuntimeObject {
	private static final long DEFAULT_WAIT = 100;

	private final CTAttachedFactory factory;
	private final CTRunEnvironment runenv;
	private LLog log;
	private final String name;

	private Set<CTToolState> tools = new HashSet<>();
	private List<CTToolUser> toolusers = new ArrayList<>();
	private List<CTFactoryState> factories = new ArrayList<>();
	private Set<CTRuntimeStepper> steppers = new HashSet<>();
	private Map<String, String> params = new HashMap<>();
	private Set<CTPartState> parts = new HashSet<>();

	private final CTPool pool;
	private CTRuntimeObject parent;
	private CTEvents events = new CTEvents();
	private LOrientation orientation;

	public CTFactoryState(final CTClient client, CTEnvironment env, final String name,
			final CTAttachedFactory factory) {
		this.factory = factory;
		this.name = name;
		runenv = new CTRunEnvironmentImpl(client, env);
		runenv.addRunObject("main", this);
		pool = new CTPool(runenv, this);
		init();
	}

	public CTFactoryState(final String name, final CTRunEnvironment runenv, final CTAttachedFactory nfactory,
			final CTFactoryState factorystate) {
		this.factory = nfactory;
		this.runenv = runenv;
		this.parent = factorystate;
		this.name = name;
		pool = new CTPool(this.runenv, this);
		init();
	}

	public CTFactoryState(CTClient client, CTEnvironment env, String name2, CTFactory factory) {
		this(client, env, name2, new CTAttachedFactory(factory));
	}

	@Override
	public PrintOut printOut() {
		PrintOut p = new PrintOut();
		p.append("factorystate");
		p.append(PrintOut.INDENT, "factory " + factory);
		p.append(PrintOut.INDENT, "transformation " + getTransformation());

		p.append(PrintOut.INDENT, "factories");
		for (CTFactoryState fs : factories) {
			p.append(PrintOut.INDENT2, fs.printOut());
		}

		p.append(PrintOut.INDENT, "parts");
		for (CTPartState ps : parts) {
			p.append(PrintOut.INDENT2, ps.printOut());
		}

		p.append(PrintOut.INDENT, "toolusers");
		for (CTToolUser tu : toolusers) {
			p.append(PrintOut.INDENT2, tu.printOut());
		}

		p.append(PrintOut.INDENT, "pool");
		p.append(PrintOut.INDENT2, pool.printOut());

		return p;
	}

	private void init() {
		orientation = factory.getOrientation();

		for (String fname : getFactory().getFactories()) {
			CTAttachedFactory f = getFactory().getFactory(fname);
			addFactory(fname, f);
		}

		CTValues values = new CTValues("factory", getFactory());
		call("start", values);
	}

	private boolean isRunning() {
		return true;
	}

	@Override
	public LOrientation getOrientation() {
		return orientation;
	}

	public Vector3f getTransformedVector(final Vector3f l) {
		Vector3f c = new Vector3f();
		getTransformation().transformVector(l, c);
		return c;
	}

	public Transform getTransformation() {
		return orientation.getTransformation();
	}

	public CTPool getPool() {
		return pool;
	}

	@Override
	public String toString() {
		return "FS[" + getFactory() + "]";
	}

	private CTFactoryState addFactory(String id, CTAttachedFactory f) {
		getLog().info("addFactory " + id + " factory:" + f);
		CTFactoryState state = new CTFactoryState(id, runenv, f, this);
		factories.add(state);
		return state;
	}

	public CTFactoryState getFactory(String id) {
		for (CTFactoryState f : factories) {
			if (f.getName().equals(id)) {
				return f;
			}
		}

		log.info("returing null factory with id " + id);

		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public CTToolState addTool(String id, CTTool tool) {
		getLog().info("addTool " + id + " tool:" + tool);
		if (tool != null) {
			CTToolState toolstate = new CTToolState(id, runenv, tool, this);
			tools.add(toolstate);
			return toolstate;
		} else {
			return null;
		}
	}

	public CTToolState getTool(String id) {
		CTToolState tool = findTool(id);
		if (tool != null) {
			ConditionWaiter.wait(() -> !tool.isInUse() || !isRunning(), 0);
			return tool;
		} else {
			return null;
		}
	}

	private CTToolState findTool(String id) {
		for (CTToolState tool : tools) {
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

	public void addToolUser(CTToolUser tooluser) {
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
			for (CTRuntimeStepper stepper : new HashSet<>(steppers)) {
				if (stepper.step(dtime)) {
					steppers.remove(stepper);
				}
			}
		}
	}

	private void stepFactories(double dtime) {
		synchronized (factories) {
			for (CTFactoryState f : factories) {
				f.step(dtime);
			}
		}
	}

	private void stepTools(double dtime) {
		synchronized (tools) {
			for (CTToolState tool : tools) {
				tool.step(dtime);
			}
		}
	}

	private void stepToolUsers(double dtime) {
		synchronized (toolusers) {
			for (CTToolUser tooluser : toolusers) {
				tooluser.step(dtime);
			}
		}
	}

	@Override
	public void stop() {
		for (CTToolUser tooluser : toolusers) {
			tooluser.stop();
		}
		for (CTFactoryState cfactory : factories) {
			cfactory.stop();
		}
	}

	public void requestMove(CTToolState ctToolState, Vector3f l, Vector3f n, double angle) {
		CTToolUser tooluser = getToolUser(ctToolState, l);
		getLog().info("requestMove " + tooluser + " " + l + " tool:" + ctToolState);
		tooluser.setTool(ctToolState);
		tooluser.move(l, n, angle);
	}

	public CTToolUser getToolUser(final CTToolState ctToolState, Vector3f l) {
		ConditionWaiter.wait(() -> !isRunning() || getAvailableToolUser(ctToolState) != null, 0);
		return getAvailableToolUser(ctToolState, l);
	}

	private CTToolUser getAvailableToolUser(CTToolState toolstate) {
		for (CTToolUser tooluser : toolusers) {
			if (tooluser.isAvailable(toolstate)) {
				return tooluser;
			}
		}
		return null;
	}

	private CTToolUser getAvailableToolUser(CTToolState toolstate, Vector3f l) {
		ArrayList<CTToolUser> ts = new ArrayList<>(toolusers);
		ts.sort(new Comparator<CTToolUser>() {
			@Override
			public int compare(CTToolUser a, CTToolUser b) {
				double adistance = a.getOrientation().getLocation().subtract(l).length();
				double bdistance = b.getOrientation().getLocation().subtract(l).length();
				if (adistance < bdistance) {
					return -1;
				} else if (adistance > bdistance) {
					return 1;
				} else {
					return 0;
				}
			}
		});

		for (CTToolUser tooluser : ts) {
			if (tooluser.isAvailable(toolstate)) {
				return tooluser;
			}
		}
		return null;
	}

	public List<CTToolUser> getToolUsers() {
		return new ArrayList<>(toolusers);
	}

	public CTRunEnvironment getRunEnvironment() {
		return this.runenv;
	}

	public CTEnvironmentTask addTask(String task, CTValues values) throws CTRuntimeError {
		CTScriptRunnerImpl script = getScript(task);
		if (script != null) {
			return runenv.addTask(script, values);
		} else {
			String message = "addTaks failed. No script called " + task;
			log.info(message);
			throw new CTRuntimeError(message);
		}
	}

	public boolean call(String string) {
		return call(string, new CTValues());
	}

	public boolean call(String string, CTValues values) {
		CTScriptRunnerImpl s = getScript(string);
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

	@Override
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

	private CTScriptRunnerImpl getScript(String string) {
		CTScript script = runenv.getEnvironment().getScript(string);
		if (script == null) {
			script = getFactory().getScript(string);
		}

		return pool.getScript(script);
	}

	public CTFactory getFactory() {
		return factory.getFactory();
	}

	public Set<CTPartState> getParts() {
		synchronized (parts) {
			return new HashSet<>(this.parts);
		}
	}

	public CTPart getPart(String s) {
		return runenv.getClient().getObjectFactory().getPart(new WStringID(s));
	}

	public String readStorage(String path) {
		return runenv.getClient().getStorage().readStorage(path);
	}

	public CTPartState newPart() {
		CTPartState partstate = new CTPartState(this, runenv.getClient().getObjectFactory().getPart());
		addPart(partstate);
		return partstate;
	}

	private void addPart(CTPartState partstate) {
		synchronized (parts) {
			parts.add(partstate);
		}
	}

	public void remove(CTPartState partstate) {
		synchronized (parts) {
			parts.remove(partstate);
		}
	}

	public List<CTFactoryState> getFactories() {
		return new ArrayList<>(this.factories);
	}

	public Vector3f getVector(String name) {
		return getFactory().getEnvironment().getVectorParameter(name);
	}

	// TODO FIXME
	/**
	 * Will be removed in future version.
	 */
	public void addSuperheroRobot() {
		ReallySimpleSuperheroRobot tooluser = new ReallySimpleSuperheroRobot(runenv, this,
				getFactory().getToolUserSpawnLocation());
		addToolUser(tooluser);
	}

	public void stepWhile(CTRuntimeStepper r) {
		log.info("stepWhile " + r);
		addStepper(r);
		waitStepperDone(r);
	}

	private void waitStepperDone(CTRuntimeStepper r) {
		try {
			while (!isStepperDone(r)) {
				synchronized (steppers) {
					steppers.wait(DEFAULT_WAIT);
				}
			}
		} catch (InterruptedException e) {
			log.error(this, "waitStepperDone", e);
			Thread.currentThread().interrupt();
		}
	}

	private boolean isStepperDone(CTRuntimeStepper r) {
		synchronized (steppers) {
			for (CTRuntimeStepper stepper : steppers) {
				if (stepper == r) {
					return false;
				}
			}
		}
		return true;
	}

	private void addStepper(CTRuntimeStepper s) {
		synchronized (steppers) {
			steppers.add(s);
		}
	}

	private void addEvent(String event, CTValues callvalues) {
		CTRuntimeEvent e = new CTRuntimeEvent(this, event, callvalues);
		events.add(e);
	}

	public CTEvents getEvents() {
		return events;
	}
}
