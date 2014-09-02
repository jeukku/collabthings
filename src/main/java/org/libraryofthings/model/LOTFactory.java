package org.libraryofthings.model;

import java.util.HashMap;
import java.util.Map;

import org.libraryofthings.LOTClient;
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.math.LVector;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public final class LOTFactory implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "factory";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";
	private static final String VALUENAME_LOCATION = "location";
	//
	private static int counter = 0;
	//
	private ServiceObject o;
	private String name = "factory" + (LOTFactory.counter++);
	private LOTClient client;
	private LOTEnvironment env;
	//
	private LVector location = new LVector();
	private Map<String, LOTFactory> factories = new HashMap<>();

	public LOTFactory(final LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		addScript("start", new LOTScript(client));
	}

	public LOTFactory(final LOTClient nclient, final MStringID id) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		b.addValue(VALUENAME_ENVIRONMENTID, env.getID());
		JBean locationbean = location.getBean();
		locationbean.setName(VALUENAME_LOCATION);
		b.add(locationbean);
		//
		return b;
	}

	public LOTScript getScript(String string) {
		return env.getScript(string.toLowerCase());
	}

	@Override
	public boolean parseBean(JBean bean) {
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		//
		location = new LVector(bean.get(VALUENAME_LOCATION));
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
		if (!env.isReady()) {
			return false;
		}

		return true;
	}

	public void addScript(String scriptname, LOTScript lotScript) {
		env.addScript(scriptname.toLowerCase(), lotScript);
	}

	public void save() {
		env.save();
		getServiceObject().save();
	}

	public void publish() {
		getEnvironment().publish();
		getServiceObject().publish();
	}

	@Override
	public String toString() {
		return "LOTFactory[" + name + "]";
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
		if (obj instanceof LOTFactory) {
			LOTFactory fact = (LOTFactory) obj;
			return getBean().toText().equals(fact.getBean().toText());
		} else {
			return false;
		}
	}

	public LOTFactoryState initRuntimeEnvironment(LOTClient client,
			LOTEnvironment env2) {
		LOTRunEnvironmentImpl impl = new LOTRunEnvironmentImpl(client, env2);
		LOTFactoryState state = new LOTFactoryState("main", impl, this, null);
		impl.addRunObject("main", state);
		return state;
	}

	public void addFactory(final String factoryname,
			final LOTFactory childfactory) {
		this.factories.put(factoryname, childfactory);
	}

	public LOTFactory getFactory(final String name) {
		return this.factories.get(name);
	}

	public void setLocation(LVector nloc) {
		this.location = nloc.copy();
	}

	public LOTTool getTool(String name) {
		return env.getTool(name);
	}

	public String getParameter(String name) {
		return getEnvironment().getParameter(name);
	}
}
