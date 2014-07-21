package org.libraryofthings.environment;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTToolException;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;

public class LOTToolState {

	private RunEnvironment env;
	private LOTTool tool;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
	//
	private LLog log = LLog.getLogger(this);
	private boolean inuse;

	public LOTToolState(final RunEnvironment runenv, final LOTTool ntool) {
		log.info("LOTToolState with " + runenv + " tool:" + tool);
		this.env = new LOTRunEnvironmentImpl(runenv, ntool.getEnvironment());
		this.tool = ntool;
	}

	public void addTask(final String name, final Object... params) {
		env.addTask(tool.getScript(name), populateParameters(params));
	}

	public void call(final String scriptname, final Object... params)
			throws LOTToolException {
		log.info("call " + scriptname + " params:" + params);

		Object[] l = populateParameters(params);

		LOTScript script = tool.getScript(scriptname);

		if (script != null) {
			log.info("calling " + script + " with " + l);
			script.run(env, l);
		} else {
			throw new LOTToolException("Script called '" + scriptname
					+ "' does not exist in " + this);
		}
	}

	private Object[] populateParameters(final Object... params) {
		List<Object> l = new LinkedList<Object>();
		l.add(this);
		for (Object o : params) {
			addParameterToList(l, o);
		}
		return l.toArray();
	}

	private void addParameterToList(List<Object> l, Object o) {
		if (o instanceof Object[]) {
			Object[] oa = (Object[]) o;
			for (Object object : oa) {
				addParameterToList(l, object);
			}
		} else {
			l.add(o);
		}
	}

	public void moveTo(LVector l) {
		moveTo(location, normal);
	}

	public void moveTo(LVector l, LVector n) {
		this.env.getParent().requestMove(this, l, n);
	}

	public LVector getLocation() {
		return location;
	}

	public void setLocation(LVector l, LVector n) {
		this.location.set(l);
		this.normal.set(n);
	}

	public void setAvailable() {
		this.inuse = false;
	}

	public void setInUse() {
		this.inuse = true;
	}

	public boolean isInUse() {
		return this.inuse;
	}

	@Override
	public String toString() {
		return "LOTToolState " + this.tool;
	}

	public RunEnvironment getEnvironment() {
		return env;
	}

	public LOTPool getPool() {
		return getEnvironment().getPool();
	}
}
