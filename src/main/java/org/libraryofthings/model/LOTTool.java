package org.libraryofthings.model;

import org.libraryofthings.LOTClient;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public final class LOTTool implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "tool";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";
	//
	public static int counter = 0;
	//
	private ServiceObject o;
	private String name = "tool" + (LOTTool.counter++);
	private LOTPart part;
	private LOTClient client;
	private LOTEnvironment env;

	public LOTTool(final LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
	}

	public LOTTool(final LOTClient nclient, final MStringID id) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		if (part != null) {
			b.addValue(VALUENAME_MODELID, part.getServiceObject().getID());
		}
		b.addValue(VALUENAME_ENVIRONMENTID, env.getID());
		//

		return b;
	}

	public LOTScript getScript(String string) {
		return env.getScript(string);
	}

	@Override
	public boolean parseBean(JBean bean) {
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		if (modelid != null) {
			part = newPart();
			part.load(modelid);
		}
		//
		env = new LOTEnvironmentImpl(client,
				bean.getIDValue(VALUENAME_ENVIRONMENTID));
		//
		return getName() != null;
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public String getName() {
		return name;
	}

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

	public void addScript(String scriptname, LOTScript lotScript) {
		env.addScript(scriptname, lotScript);
	}

	public void save() {
		if (part != null) {
			part.save();
		}

		env.save();

		getServiceObject().save();
	}

	public void publish() {
		if (part != null) {
			part.publish();
		}

		getEnvironment().publish();

		getServiceObject().publish();
	}

	public LOTPart getPart() {
		return part;
	}

	public LOTPart newPart() {
		part = new LOTPart(client);
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
		if (obj instanceof LOTTool) {
			LOTTool tool = (LOTTool) obj;
			return getBean().toText().equals(tool.getBean().toText());
		} else {
			return false;
		}
	}
}
