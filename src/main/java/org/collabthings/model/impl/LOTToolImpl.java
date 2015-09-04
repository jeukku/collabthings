package org.collabthings.model.impl;

import org.collabthings.LOTClient;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTObject;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

public final class LOTToolImpl implements ServiceObjectData, LOTObject, LOTTool {
	private static final String BEANNAME = "tool";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";

	private static int counter = 0;
	private ServiceObject o;
	private String name = "tool" + (LOTToolImpl.counter++);
	private LOTPart part;
	private LOTClient client;
	private LOTEnvironment env;

	public LOTToolImpl(final LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		addScript("draw");
	}

	public LOTToolImpl(final LOTClient nclient, final MStringID id) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		o.load(id);
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		if (part != null) {
			b.addValue(VALUENAME_MODELID, part.getID());
		}
		b.addValue(VALUENAME_ENVIRONMENTID, env.getID());
		//

		return b;
	}

	@Override
	public LOTScript getScript(String string) {
		return env.getScript(string.toLowerCase());
	}

	@Override
	public boolean parseBean(WData bean) {
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		if (modelid != null) {
			part = newPart();
			LOTPartImpl partimpl = (LOTPartImpl) part;
			partimpl.load(modelid);
		}
		//
		env = new LOTEnvironmentImpl(client,
				bean.getIDValue(VALUENAME_ENVIRONMENTID));
		//
		return getName() != null;
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public ObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String nname) {
		this.name = nname;
	}

	@Override
	public boolean isReady() {
		if (part != null && !part.isReady()) {
			return false;
		}

		if (!env.isReady()) {
			return false;
		}

		return true;
	}

	@Override
	public LOTScript addScript(String string) {
		return addScript(string, new LOTScriptImpl(client));
	}

	public LOTScript addScript(String scriptname, LOTScript lotScript) {
		env.addScript(scriptname.toLowerCase(), lotScript);
		return lotScript;
	}

	@Override
	public void save() {
		if (part != null) {
			part.save();
		}

		env.save();

		getServiceObject().save();
	}

	@Override
	public void publish() {
		if (part != null) {
			part.publish();
		}

		getEnvironment().publish();

		getServiceObject().publish();
	}

	@Override
	public LOTPart getPart() {
		return part;
	}

	@Override
	public LOTPart newPart() {
		part = new LOTPartImpl(client);
		return part;
	}

	@Override
	public String toString() {
		return "LOTTool[" + name + "]";
	}

	public LOTEnvironment getEnvironment() {
		if (this.env == null) {
			this.env = new LOTEnvironmentImpl(client);
		}
		return this.env;
	}

	@Override
	public int hashCode() {
		return getBean().toText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LOTToolImpl) {
			LOTToolImpl tool = (LOTToolImpl) obj;
			return getBean().toText().equals(tool.getBean().toText());
		} else {
			return false;
		}
	}

}
