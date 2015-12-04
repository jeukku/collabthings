package org.collabthings.factory.impl;

import org.collabthings.LOTClient;
import org.collabthings.environment.impl.LOTScriptInvoker;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.model.LOTScript;
import org.collabthings.model.impl.LOTEnvironmentImpl;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public class LOTPartBuilderImpl implements LOTPartBuilder, ServiceObjectData {
	private static final String VALUE_SCRIPT = "script";

	public static final String BEANNAME = "factory";

	private LOTClient client;
	private LOTScript script;
	private String error;

	private ServiceObject o;

	public LOTPartBuilderImpl(LOTClient client) {
		this.client = client;
		o = new ServiceObject(BEANNAME, client.getClient(), this,
				client.getVersion(), client.getPrefix());
	}

	@Override
	public boolean run(LOTPart p) {
		LOTScriptInvoker inv = new LOTScriptInvoker(script);
		LOTEnvironment e = new LOTEnvironmentImpl(client);
		boolean ret = inv.run("run", p, e);
		error = inv.getError();
		return ret;
	}

	public String getError() {
		return error;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setScript(LOTScript s) {
		this.script = s;
	}

	@Override
	public void publish() {
		script.publish();
		o.publish();
	}

	@Override
	public void save() {
		script.save();
		o.save();
	}

	@Override
	public ObjectID getID() {
		return o.getID();
	}

	@Override
	public WObject getObject() {
		WObject content = o.getBean();

		content.addValue(VALUE_SCRIPT, this.script.getID());
		return content;
	}

	@Override
	public boolean parse(WObject o) {
		String scriptid = o.getValue(VALUE_SCRIPT);
		setScript(client.getObjectFactory().getScript(new MStringID(scriptid)));
		return true;
	}

	public boolean load(MStringID builderid) {
		o = new ServiceObject(BEANNAME, client.getClient(), this,
				client.getVersion(), client.getPrefix());
		return o.load(builderid);
	}

	@Override
	public int hashCode() {
		return getObject().toText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LOTPartBuilderImpl) {
			LOTPartBuilderImpl builder = (LOTPartBuilderImpl) obj;
			return getObject().toText().equals(builder.getObject().toText());
		} else {
			return false;
		}
	}

}
