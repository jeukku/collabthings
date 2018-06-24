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

import org.collabthings.CTToolException;
import org.collabthings.application.CTApplicationRunner;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTRuntimeObject;
import org.collabthings.model.CTTool;
import org.collabthings.model.CTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

public class CTToolState implements CTRuntimeObject {

	private CTRunEnvironment env;
	private CTFactoryState factory;

	private final LOrientation o = new LOrientation();
	private CTTool tool;
	//
	private LLog log = LLog.getLogger(this);
	private boolean inuse;
	private String name;
	private final CTPool pool;
	private CTEvents events = new CTEvents();

	public CTToolState(final String name, final CTRunEnvironment runenv, final CTTool ntool,
			final CTFactoryState factorystate) {
		log.info("CTToolState with " + runenv + " tool:" + ntool.getName());

		this.name = name;
		this.factory = factorystate;
		this.env = runenv;
		this.tool = ntool;
		this.factory = factorystate;
		pool = new CTPool(runenv, this);
	}

	@Override
	public PrintOut printOut() {
		PrintOut out = new PrintOut();

		out.append("toolstate");
		out.append(1, "" + out);
		return out;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void step(double dtime) {
		// nothing to do
	}

	@Override
	public void stop() {
		// nothing to do
	}

	@Override
	public String getParameter(String name) {
		return factory.getParameter(name);
	}

	public void call(final String scriptname, final CTValues values) throws CTToolException {

		CTValues callvalues = values != null ? values.copy() : new CTValues();

		callvalues.put("tool", this);

		CTApplicationRunner script = pool.getApplication(tool.getApplication(scriptname));
		// TODO shouldn't be hard coded like this
		if (!"draw".equals(scriptname)) {
			this.env.recordEvent(this, "calling " + scriptname + " " + script, callvalues);
			events.add(new CTRuntimeEvent(this, "" + scriptname, callvalues));
		}

		if (script != null) {
			script.run(env, callvalues);
		} else {
			throw new CTToolException("Application called '" + scriptname + "' does not exist in " + this);
		}
	}

	public void moveTo(Vector3f l) {
		moveTo(l, o.getNormal(), o.getAngle());
	}

	public void moveTo(Vector3f l, Vector3f n, double angle) {
		log.info("moveTo " + l + " " + n);
		this.factory.requestMove(this, l, n, angle);
	}

	@Override
	public LOrientation getOrientation() {
		return o;
	}

	public Vector3f getLocation() {
		return o.getLocation();
	}

	public void setOrientation(Vector3f l, Vector3f n, double angle) {
		o.getLocation().set(l);
		o.getNormal().set(n);
		o.setAngle(angle);
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

	public CTPool getPool() {
		return pool;
	}

	@Override
	public String toString() {
		return "CTToolState[" + this.tool + "][" + this.o + "]";
	}

	public CTEvents getEvents() {
		return events;
	}
}
