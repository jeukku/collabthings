package org.libraryofthings.environment;

import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTTool;

public class LOTToolState {

	private LOTEnvironment env;
	private LOTTool tool;
	private LVector location = new LVector();
	//
	private LLog log = LLog.getLogger(this);

	public LOTToolState(final LOTEnvironment nenv, final LOTTool ntool) {
		this.env = nenv;
		this.tool = ntool;
	}

	public void call(final RunEnvironment runenv, final String nname,
			final Object... params) throws NoSuchMethodException,
			ScriptException {

		List<Object> l = new LinkedList<Object>();
		l.add(this);
		for (Object o : params) {
			l.add(o);
		}

		tool.call(runenv, nname, l.toArray());
	}

	public void moveTo(LVector l) {
		log.info("moving to " + l);
	}

	public void moveTo(LVector l, LVector n) {
		log.info("moving to l:" + l + " n:" + n);
	}

	public LVector getLocation() {
		return location;
	}
}
