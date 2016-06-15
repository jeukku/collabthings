package org.collabthings.model.impl;

import org.collabthings.CTClient;
import org.collabthings.environment.impl.CTScriptInvoker;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public class CTPartBuilderImpl implements CTPartBuilder, ServiceObjectData {
	public static final String BEANNAME = "partbuilder";

	private static final String VALUE_SCRIPT = "script";
	private static final String VALUE_NAME = "name";
	private static final String VALUE_ENV = "env";

	private CTClient client;
	private CTScript script;
	private String error;

	private ServiceObject o;

	private String name;

	private CTEnvironment e;

	public CTPartBuilderImpl(CTClient client) {
		this.client = client;
		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
		e = new CTEnvironmentImpl(client);
	}

	@Override
	public boolean run(CTPart p) {
		CTScriptInvoker inv = new CTScriptInvoker(script);
		boolean ret = inv.run("run", e, p);
		error = inv.getError();
		return ret;
	}

	public String getError() {
		return error;
	}

	@Override
	public void setName(String string) {
		this.name = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public CTScript getScript() {
		return this.script;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setScript(CTScript s) {
		this.script = s;
	}

	@Override
	public void publish() {
		save();

		script.publish();
		e.publish();
		o.publish();
		client.publish(getName(), this);
	}

	@Override
	public void save() {
		script.save();
		e.save();
		o.save();
	}

	@Override
	public ObjectID getID() {
		return o.getID();
	}

	@Override
	public WObject getObject() {
		WObject content = o.getBean();

		if (script != null) {
			content.addValue(VALUE_SCRIPT, this.script.getID());
		}

		content.addValue(VALUE_NAME, getName());
		content.addValue(VALUE_ENV, e.getID().toString());

		return content;
	}

	@Override
	public boolean parse(WObject o) {
		MStringID scriptid = new MStringID(o.getValue(VALUE_SCRIPT));
		setScript(client.getObjectFactory().getScript(scriptid));
		setName(o.getValue(VALUE_NAME));
		e = new CTEnvironmentImpl(client, new MStringID(o.getValue(VALUE_ENV)));
		return true;
	}

	public boolean load(MStringID builderid) {
		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
		return o.load(builderid);
	}

	@Override
	public int hashCode() {
		return getObject().toText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CTPartBuilderImpl) {
			CTPartBuilderImpl builder = (CTPartBuilderImpl) obj;
			return getObject().toText().equals(builder.getObject().toText());
		} else {
			return false;
		}
	}

}
