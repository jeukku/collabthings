package org.libraryofthings.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOT3DModel;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.client.model.MID;
import waazdoh.util.MStringID;

public final class LOTFactoryImpl implements ServiceObjectData, LOTFactory {
	private static final String BEANNAME = "factory";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";
	private static final String VALUENAME_LOCATION = "location";
	//
	private static int counter = 0;
	//
	private ServiceObject o;
	private String name = "factory" + (LOTFactoryImpl.counter++);
	private LOTClient client;
	private LOTEnvironment env;
	//
	private LVector location = new LVector();
	private Map<String, LOTFactoryImpl> factories = new HashMap<>();
	private LOTBoundingBox bbox;
	private LOT3DModel model;

	public LOTFactoryImpl(final LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		addScript("start", new LOTScriptImpl(client));
	}

	public LOTFactoryImpl(final LOTClient nclient, final MStringID id) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
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
		if (bbox != null) {
			b.add(bbox.getBean());
		}
		if (model != null) {
			b.addValue(VALUENAME_MODELID, model.getID());
		}
		JBean bchildfactories = b.add("factories");
		for (String cfname : factories.keySet()) {
			LOTFactoryImpl cf = factories.get(cfname);
			bchildfactories.addValue(cfname, cf.getID().toString());
		}
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
		if (modelid != null) {
			model = client.getObjectFactory().getModel(modelid);
		}
		location = new LVector(bean.get(VALUENAME_LOCATION));
		env = new LOTEnvironmentImpl(client,
				bean.getIDValue(VALUENAME_ENVIRONMENTID));
		JBean bbbox = bean.get(LOTBoundingBox.BEAN_NAME);
		if (bbbox != null) {
			bbox = new LOTBoundingBox(bbbox);
		}
		JBean bchildfactories = bean.get("factories");
		List<JBean> bcfs = bchildfactories.getChildren();
		for (JBean bchildfactory : bcfs) {
			String cfname = bchildfactory.getName();
			addFactory(cfname, new LOTFactoryImpl(this.client, new MStringID(
					bchildfactory.getText())));
		}
		return getName() != null;
	}

	@Override
	public LOTScript addScript(String string) {
		return addScript(string, new LOTScriptImpl(client));
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public MID getID() {
		return getServiceObject().getID();
	}

	public String getName() {
		return name;
	}

	public void setName(final String nname) {
		this.name = nname;
	}

	@Override
	public LOTBoundingBox getBoundingBox() {
		return bbox;
	}

	@Override
	public void setBoundingBox(LVector a, LVector b) {
		bbox = new LOTBoundingBox(a, b);
	}

	@Override
	public boolean isReady() {
		if (!env.isReady()) {
			return false;
		}

		return true;
	}

	public LOTScript addScript(String scriptname, LOTScript lotScript) {
		env.addScript(scriptname.toLowerCase(), lotScript);
		return lotScript;
	}

	public void save() {
		env.save();
		getServiceObject().save();
		for (LOTFactoryImpl cf : factories.values()) {
			cf.save();
		}
	}

	public void publish() {
		save();

		getEnvironment().publish();
		getServiceObject().publish();
		for (LOTFactoryImpl cf : factories.values()) {
			cf.publish();
		}
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
		if (obj instanceof LOTFactoryImpl) {
			LOTFactoryImpl fact = (LOTFactoryImpl) obj;
			return getBean().toText().equals(fact.getBean().toText());
		} else {
			return false;
		}
	}

	@Override
	public LOTFactory addFactory(String string) {
		return addFactory(string, new LOTFactoryImpl(client));
	}

	public LOTFactory addFactory(final String factoryname,
			final LOTFactoryImpl childfactory) {
		this.factories.put(factoryname, childfactory);
		return childfactory;
	}

	public LOTFactoryImpl getFactory(final String name) {
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

	public void setModel(LOT3DModel model) {
		this.model = model;
	}
}
