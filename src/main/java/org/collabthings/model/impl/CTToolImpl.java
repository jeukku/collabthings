package org.collabthings.model.impl;

import org.collabthings.CTClient;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTObject;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public final class CTToolImpl implements ServiceObjectData, CTObject, CTTool {
	private static final String BEANNAME = "tool";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";

	private static int counter = 0;
	private ServiceObject o;
	private String name = "tool" + (CTToolImpl.counter++);
	private CTPart part;
	private CTClient client;
	private CTEnvironment env;

	public CTToolImpl(final CTClient nclient) {
		this.client = nclient;
		env = new CTEnvironmentImpl(nclient);
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
		addScript("draw");
	}

	public CTToolImpl(final CTClient nclient, final MStringID id) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
		o.load(id);
	}

	@Override
	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public WObject getObject() {
		WObject b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		if (part != null) {
			b.addValue(VALUENAME_MODELID, part.getID());
		}
		b.addValue(VALUENAME_ENVIRONMENTID, env.getID());
		//

		return b;
	}

	@Override
	public CTScript getScript(String string) {
		return env.getScript(string.toLowerCase());
	}

	@Override
	public boolean parse(WObject bean) {
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		if (modelid != null) {
			part = newPart();
			CTPartImpl partimpl = (CTPartImpl) part;
			partimpl.load(modelid);
		}
		//
		env = new CTEnvironmentImpl(client, bean.getIDValue(VALUENAME_ENVIRONMENTID));
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
	public CTScript addScript(String string) {
		return addScript(string, new CTScriptImpl(client));
	}

	public CTScript addScript(String scriptname, CTScript ctScript) {
		env.addScript(scriptname.toLowerCase(), ctScript);
		return ctScript;
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
	public CTPart getPart() {
		return part;
	}

	@Override
	public CTPart newPart() {
		part = new CTPartImpl(client);
		return part;
	}

	@Override
	public String toString() {
		return "CTTool[" + name + "]";
	}

	public CTEnvironment getEnvironment() {
		if (this.env == null) {
			this.env = new CTEnvironmentImpl(client);
		}
		return this.env;
	}

	@Override
	public int hashCode() {
		return getObject().toYaml().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CTToolImpl) {
			CTToolImpl tool = (CTToolImpl) obj;
			return getObject().toYaml().equals(tool.getObject().toYaml());
		} else {
			return false;
		}
	}

}