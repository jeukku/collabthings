package org.libraryofthings.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LOrientation;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOT3DModel;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTEnvironment;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

public final class LOTFactoryImpl implements ServiceObjectData, LOTFactory {
	public static final String BEANNAME = "factory";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";
	private static final String VALUENAME_SPAWNLOCATION = "toolspawn";
	private static final String BEANNAME_ORIENTATION = "orientation";
	//
	private static int counter = 0;
	//
	private ServiceObject o;
	private String name = "factory" + (LOTFactoryImpl.counter++);
	private LOTClient client;
	private LOTEnvironment env;
	//
	private Map<String, LOTFactory> factories;
	private final LOTBoundingBox bbox = new LOTBoundingBox(new LVector(), new LVector());
	private LOT3DModel model;
	private LVector tooluserspawnlocation;
	// orientation and location
	private LOrientation orientation = new LOrientation();

	// used as a proxy
	private WData bean;

	public LOTFactoryImpl(final LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		factories = new HashMap<String, LOTFactory>();

		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(),
				nclient.getPrefix());
		addScript("start", new LOTScriptImpl(client));
		setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));
	}

	public LOTFactoryImpl(final LOTClient nclient, final MStringID id) {
		this.client = nclient;
		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(),
				nclient.getPrefix());
		o.load(id);
	}

	public long getModifyTime() {
		return o.getModifytime();
	}

	public long getCreationTime() {
		return o.getCreationtime();
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		b.addValue(VALUENAME_ENVIRONMENTID, getEnv().getID());
		addVectorBean(b, VALUENAME_SPAWNLOCATION, tooluserspawnlocation);
		b.add(orientation.getBean(BEANNAME_ORIENTATION));

		if (bbox != null) {
			b.add(bbox.getBean());
		}
		if (getModel() != null) {
			b.addValue(VALUENAME_MODELID, model.getID());
		}
		WData bchildfactories = b.add("factories");
		for (String cname : getFactoryMap().keySet()) {
			LOTFactory cf = getFactory(cname);
			bchildfactories.addValue(cname, cf.getID().toString());
		}
		//
		return b;
	}

	private void addVectorBean(WData b, String valuename, LVector v) {
		if (v != null) {
			WData vectorbean = v.getBean(valuename);
			b.add(vectorbean);
		}
	}

	@Override
	public boolean parseBean(WData bean) {
		this.bean = bean;
		setName(bean.getValue(VALUENAME_NAME));

		WData beansl = bean.get(VALUENAME_SPAWNLOCATION);
		if (beansl != null) {
			tooluserspawnlocation = new LVector(beansl);
		}

		WData ob = bean.get(BEANNAME_ORIENTATION);
		orientation = new LOrientation(ob);

		WData bbbox = bean.get(LOTBoundingBox.BEAN_NAME);
		if (bbbox != null) {
			bbox.set(bbbox);
		}

		return getName() != null;
	}

	@Override
	public LOTScript addScript(String string) {
		return addScript(string, new LOTScriptImpl(client));
	}

	@Override
	public Set<String> getScripts() {
		return getEnv().getScripts();
	}

	public LOTScript getScript(String string) {
		return getEnv().getScript(string.toLowerCase());
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public ObjectID getID() {
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

	public void setBoundingBox(LOTBoundingBox box) {
		this.bbox.set(box.getA(), box.getB());
	}

	@Override
	public void setBoundingBox(LVector a, LVector b) {
		bbox.set(a, b);
	}

	@Override
	public boolean isReady() {
		if (!getEnv().isReady()) {
			return false;
		}

		return true;
	}

	public LOTScript addScript(String scriptname, LOTScript lotScript) {
		getEnv().addScript(scriptname.toLowerCase(), lotScript);
		return lotScript;
	}

	public void save() {
		if (getModel() != null) {
			model.save();
		}

		getEnvironment().save();
		getServiceObject().save();

		for (LOTFactory cf : getFactoryMap().values()) {
			cf.save();
		}
	}

	public LOT3DModel getModel() {
		if (model == null && bean != null) {
			MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
			if (modelid != null) {
				model = client.getObjectFactory().getModel(modelid);
			}
		}
		return model;
	}

	public void publish() {
		save();

		if (model != null) {
			model.publish();
		}

		for (LOTFactory cf : getFactoryMap().values()) {
			cf.publish();
		}

		getEnvironment().publish();
		getServiceObject().publish();
	}

	@Override
	public String toString() {
		return "F[" + name + "]";
	}

	public LOTEnvironment getEnvironment() {
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
	public LOTFactory addFactory() {
		return addFactory("child" + this.getFactoryMap().size());
	}

	@Override
	public LOTFactory addFactory(String string) {
		LOTFactoryImpl childfactory = new LOTFactoryImpl(client);
		childfactory.setBoundingBox(getBoundingBox().getA().getScaled(0.5), getBoundingBox().getB()
				.getScaled(0.5));
		return addFactory(string, childfactory);
	}

	@Override
	public LOTFactory addFactory(final String factoryname, final LOTFactory childfactory) {
		this.getFactoryMap().put(factoryname, childfactory);
		return childfactory;
	}

	public LOTFactory getFactory(final String name) {
		return getFactoryMap().get(name);
	}

	@Override
	public Set<String> getFactories() {
		return new HashSet<>(getFactoryMap().keySet());
	}

	private Map<String, LOTFactory> getFactoryMap() {
		if (factories == null) {
			factories = new HashMap<String, LOTFactory>();

			WData bchildfactories = bean.get("factories");
			List<WData> bcfs = bchildfactories.getChildren();
			for (WData bchildfactory : bcfs) {
				String cfname = bchildfactory.getName();
				addFactory(cfname,
						new LOTFactoryImpl(this.client, new MStringID(bchildfactory.getText())));
			}
		}

		return factories;
	}

	public void setLocation(LVector nloc) {
		orientation.getLocation().set(nloc);
	}

	public LOrientation getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(LVector n, double d) {
		orientation.set(n, d);
	}

	@Override
	public LTransformation getTransformation() {
		return new LTransformation(orientation);
	}

	public LOTTool getTool(String name) {
		return getEnv().getTool(name);
	}

	private LOTEnvironment getEnv() {
		if (env == null) {
			env = new LOTEnvironmentImpl(client, bean.getIDValue(VALUENAME_ENVIRONMENTID));
		}
		return env;
	}

	public void setModel(LOT3DModel model) {
		this.model = model;
	}

	@Override
	public void setToolUserSpawnLocation(LVector spawnlocation) {
		this.tooluserspawnlocation = spawnlocation;
	}

	@Override
	public LVector getToolUserSpawnLocation() {
		if (tooluserspawnlocation != null) {
			return tooluserspawnlocation;
		} else {
			return null;
		}
	}
}
