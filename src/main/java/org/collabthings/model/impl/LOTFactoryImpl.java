package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.LOTClient;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTAttachedFactory;
import org.collabthings.model.LOTBinaryModel;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.LOTScript;
import org.collabthings.model.LOTTool;
import org.collabthings.util.PrintOut;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WLogger;
import waazdoh.common.WObject;

public final class LOTFactoryImpl implements ServiceObjectData, LOTFactory {
	public static final String BEANNAME = "factory";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";
	private static final String VALUENAME_SPAWNLOCATION = "toolspawn";
	//
	private static int counter = 0;
	//
	private ServiceObject o;
	private String name = "factory" + (LOTFactoryImpl.counter++);
	private LOTClient client;
	private LOTEnvironment env;
	//
	private Map<String, LOTAttachedFactory> factories;
	private final LOTBoundingBox bbox = new LOTBoundingBox(new LVector(),
			new LVector());
	private LOTBinaryModel model;
	private LVector tooluserspawnlocation = new LVector();

	// used as a proxy
	private WObject bean;

	public LOTFactoryImpl(final LOTClient nclient) {
		this.client = nclient;
		env = new LOTEnvironmentImpl(nclient);
		factories = new HashMap<String, LOTAttachedFactory>();

		o = new ServiceObject(BEANNAME, nclient.getClient(), this,
				nclient.getVersion(), nclient.getPrefix());
		addScript("start", new LOTScriptImpl(client));
		setBoundingBox(new LVector(-1, -1, -1), new LVector(1, 1, 1));
	}

	public boolean load(MStringID id) {
		env = null;
		name = null;
		factories = null;

		o = new ServiceObject(BEANNAME, client.getClient(), this,
				client.getVersion(), client.getPrefix());
		return o.load(id);
	}

	@Override
	public PrintOut printOut() {
		PrintOut p = new PrintOut();
		p.append("Factory " + getName());
		p.append(1, env.printOut());
		p.append(1, "" + factories.keySet());
		return p;
	}

	public long getModifyTime() {
		return o.getModifytime();
	}

	public long getCreationTime() {
		return o.getCreationtime();
	}

	@Override
	public WObject getObject() {
		WObject org = o.getBean();
		WObject b = org.add("content");
		b.addValue(VALUENAME_NAME, getName());

		if (getEnv() != null) {
			b.addValue(VALUENAME_ENVIRONMENTID, getEnv().getID());
		}

		addVectorBean(b, VALUENAME_SPAWNLOCATION, tooluserspawnlocation);

		if (bbox != null) {
			b.add(LOTBoundingBox.BEAN_NAME, bbox.getBean());
		}

		if (getModel() != null) {
			b.addValue(VALUENAME_MODELID, model.getID());
		}

		for (String cname : getFactoryMap().keySet()) {
			LOTAttachedFactory cf = getFactory(cname);
			WObject bchildfactory = new WObject();
			bchildfactory.addValue("name", cname);
			if (cf.getBookmark() != null) {
				bchildfactory.addValue("bookmark", cf.getBookmark());
			} else {
				bchildfactory
						.addValue("id", cf.getFactory().getID().toString());
			}
			bchildfactory.add("orientation", cf.getOrientation().getBean());

			b.addToList("factories", bchildfactory);
		}
		//
		return org;
	}

	private void addVectorBean(WObject b, String valuename, LVector v) {
		if (v != null) {
			WObject vectorbean = v.getBean();
			b.add(valuename, vectorbean);
		}
	}

	@Override
	public boolean parse(WObject bean) {
		this.bean = bean;
		WObject c = getContent();
		setName(c.getValue(VALUENAME_NAME));

		WObject beansl = c.get(VALUENAME_SPAWNLOCATION);
		if (beansl != null) {
			tooluserspawnlocation = new LVector(beansl);
		}

		WObject bbbox = c.get(LOTBoundingBox.BEAN_NAME);
		if (bbbox != null) {
			bbox.set(bbbox);
		}

		return getName() != null;
	}

	private WObject getContent() {
		if (bean != null) {
			return this.bean.get("content");
		} else {
			return null;
		}
	}

	@Override
	public LOTScript addScript(String string) {
		return addScript(string, new LOTScriptImpl(client));
	}

	@Override
	public Set<String> getScripts() {
		return getEnv().getScripts();
	}

	@Override
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String nname) {
		this.name = nname;
	}

	@Override
	public LOTBoundingBox getBoundingBox() {
		return bbox;
	}

	@Override
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

	@Override
	public LOTScript addScript(String scriptname, LOTScript lotScript) {
		getEnv().addScript(scriptname.toLowerCase(), lotScript);
		return lotScript;
	}

	@Override
	public void save() {
		if (getModel() != null) {
			model.save();
		}

		getEnvironment().save();
		getServiceObject().save();

		for (LOTAttachedFactory cf : getFactoryMap().values()) {
			cf.getFactory().save();
		}
	}

	public LOTBinaryModel getModel() {
		if (model == null && bean != null) {
			MStringID modelid = getContent().getIDValue(VALUENAME_MODELID);
			if (modelid != null) {
				model = client.getObjectFactory().getModel(modelid);
			}
		}
		return model;
	}

	@Override
	public void publish() {
		save();

		if (model != null) {
			model.publish();
		}

		for (LOTAttachedFactory cf : getFactoryMap().values()) {
			cf.getFactory().publish();
		}

		getEnvironment().publish();
		getServiceObject().publish();

		client.publish(getName(), this);
	}

	@Override
	public String toString() {
		return "F[" + name + "]";
	}

	@Override
	public LOTEnvironment getEnvironment() {
		return this.env;
	}

	@Override
	public int hashCode() {
		return getObject().toText().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LOTFactoryImpl) {
			LOTFactoryImpl fact = (LOTFactoryImpl) obj;
			return getObject().toText().equals(fact.getObject().toText());
		} else {
			return false;
		}
	}

	@Override
	public LOTAttachedFactory addFactory() {
		return addFactory("child" + this.getFactoryMap().size());
	}

	@Override
	public LOTAttachedFactory addFactory(String string) {
		LOTFactoryImpl childfactory = new LOTFactoryImpl(client);
		childfactory.setBoundingBox(getBoundingBox().getA().getScaled(0.5),
				getBoundingBox().getB().getScaled(0.5));
		return addFactory(string, childfactory);
	}

	@Override
	public LOTAttachedFactory addFactory(final String factoryname,
			final LOTFactory factory) {
		LOTAttachedFactory cfactory = new LOTAttachedFactory(factory);
		this.getFactoryMap().put(factoryname, cfactory);
		return cfactory;
	}

	@Override
	public LOTAttachedFactory getFactory(final String name) {
		return getFactoryMap().get(name);
	}

	@Override
	public Set<String> getFactories() {
		return new HashSet<>(getFactoryMap().keySet());
	}

	private Map<String, LOTAttachedFactory> getFactoryMap() {
		if (factories == null && bean != null) {
			factories = new HashMap<String, LOTAttachedFactory>();

			List<WObject> bcfs = getContent().getObjectList("factories");
			for (WObject bchildfactory : bcfs) {
				LOTFactoryImpl f = new LOTFactoryImpl(this.client);

				String cfname = bchildfactory.getValue("name");
				String cfid = bchildfactory.getValue("id");
				String bookmark = bchildfactory.getValue("bookmark");

				if (cfid == null) {
					cfid = client.getPublished(bookmark);
				}

				if (f.load(new MStringID(cfid))) {
					LOTAttachedFactory cf = addFactory(cfname, f);
					cf.setBookmark(bookmark);
					cf.set(bchildfactory.get("orientation"));
				} else {
					WLogger.getLogger(this).error(
							"failed to load childfactory "
									+ bchildfactory.toText());
				}
			}
		}

		return factories;
	}

	public LOTTool getTool(String name) {
		return getEnv().getTool(name);
	}

	private LOTEnvironment getEnv() {
		if (env == null && bean != null) {
			env = new LOTEnvironmentImpl(client, getContent().getIDValue(
					VALUENAME_ENVIRONMENTID));
		}
		return env;
	}

	@Override
	public void setModel(LOTBinaryModel model) {
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
