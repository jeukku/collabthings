package org.collabthings.factory.impl;

import org.collabthings.LOTClient;
import org.collabthings.environment.impl.LOTScriptInvoker;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTValues;
import org.collabthings.model.impl.LOTEnvironmentImpl;

import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public class LOTPartBuilderImpl implements LOTPartBuilder {

	private LOTClient client;
	private LOTScript script;
	private String error;

	public LOTPartBuilderImpl(LOTClient client) {
		this.client = client;
	}

	@Override
	public boolean run(LOTPart p) {
		LOTScriptInvoker inv = new LOTScriptInvoker(script);
		LOTEnvironment e = new LOTEnvironmentImpl(client);
		boolean ret = inv.run("run", p, e);
		error = inv.getError();
		return ret;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setScript(LOTScript s) {
		this.script = s;
	}

	@Override
	public void publish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public ObjectID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WObject getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parse(WObject o) {
		// TODO Auto-generated method stub
		return false;
	}

}
