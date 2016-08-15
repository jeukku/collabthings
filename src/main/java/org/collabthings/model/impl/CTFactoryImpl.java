package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WLogger;
import waazdoh.common.WObject;

public final class CTFactoryImpl implements ServiceObjectData, CTFactory {
	public static final String BEANNAME = "factory";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "model3did";
	private static final String VALUENAME_ENVIRONMENTID = "environmentid";
	private static final String VALUENAME_SPAWNLOCATION = "toolspawn";
	//
	private static int counter = 0;
	//
	private ServiceObject o;
	private String name = "factory" + (CTFactoryImpl.counter++);
	private CTClient client;
	private CTEnvironment env;
	//
	private Map<String, CTAttachedFactory> factories;
	private final CTBoundingBox bbox = new CTBoundingBox(new Vector3f(), new Vector3f());
	private CTBinaryModel model;
	private Vector3f tooluserspawnlocation = new Vector3f();

	// used as a proxy
	private WObject bean;

	public CTFactoryImpl(final CTClient nclient) {
		this.client = nclient;
		env = new CTEnvironmentImpl(nclient);
		factories = new HashMap<String, CTAttachedFactory>();

		o = new ServiceObject(BEANNAME, nclient.getClient(), this, nclient.getVersion(), nclient.getPrefix());
		addScript("start", new CTScriptImpl(client));
		setBoundingBox(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
	}

	public boolean load(MStringID id) {
		env = null;
		name = null;
		factories = null;

		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
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

	public long getCreationTime() {
		return o.getCreationtime();
	}

	@Override
	public WObject getObject() {
		WObject org = o.getBean();
		WObject b = org.add("content");
		b.addValue(VALUENAME_NAME, getName());

		if (getEnvironment() != null) {
			b.addValue(VALUENAME_ENVIRONMENTID, getEnvironment().getID());
		}

		addVectorBean(b, VALUENAME_SPAWNLOCATION, tooluserspawnlocation);

		if (bbox != null) {
			b.add(CTBoundingBox.BEAN_NAME, bbox.getBean());
		}

		if (getModel() != null) {
			b.addValue(VALUENAME_MODELID, model.getID());
		}

		if (getFactoryMap() != null) {
			for (String cname : getFactoryMap().keySet()) {
				CTAttachedFactory cf = getFactory(cname);
				WObject bchildfactory = new WObject();
				bchildfactory.addValue("name", cname);
				if (cf.getBookmark() != null) {
					bchildfactory.addValue("bookmark", cf.getBookmark());
				} else {
					bchildfactory.addValue("id", cf.getFactory().getID().toString());
				}
				bchildfactory.add("orientation", cf.getOrientation().getBean());

				b.addToList("factories", bchildfactory);
			}
		}
		//
		return org;
	}

	private void addVectorBean(WObject b, String valuename, Vector3f v) {
		if (v != null) {
			WObject vectorbean = CTMath.getBean(v);
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
			tooluserspawnlocation = CTMath.parseVector(beansl);
		}

		WObject bbbox = c.get(CTBoundingBox.BEAN_NAME);
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
	public CTScript addScript(String string) {
		return addScript(string, new CTScriptImpl(client));
	}

	@Override
	public Set<String> getScripts() {
		return getEnvironment().getScripts();
	}

	@Override
	public CTScript getScript(String string) {
		return getEnvironment().getScript(string.toLowerCase());
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
	public CTBoundingBox getBoundingBox() {
		return bbox;
	}

	@Override
	public void setBoundingBox(CTBoundingBox box) {
		this.bbox.set(box.getA(), box.getB());
	}

	@Override
	public void setBoundingBox(Vector3f a, Vector3f b) {
		bbox.set(a, b);
	}

	@Override
	public boolean isReady() {
		if (!getEnvironment().isReady()) {
			return false;
		}

		return true;
	}

	@Override
	public CTScript addScript(String scriptname, CTScript ctScript) {
		getEnvironment().addScript(scriptname.toLowerCase(), ctScript);
		return ctScript;
	}

	@Override
	public void save() {
		if (getModel() != null) {
			model.save();
		}

		getEnvironment().save();
		getServiceObject().save();

		for (CTAttachedFactory cf : getFactoryMap().values()) {
			cf.getFactory().save();
		}
	}

	public CTBinaryModel getModel() {
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

		for (CTAttachedFactory cf : getFactoryMap().values()) {
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
	public int hashCode() {
		return getObject().toYaml().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CTFactoryImpl) {
			CTFactoryImpl fact = (CTFactoryImpl) obj;
			return getObject().toYaml().equals(fact.getObject().toYaml());
		} else {
			return false;
		}
	}

	@Override
	public CTAttachedFactory addFactory() {
		return addFactory("child" + this.getFactoryMap().size());
	}

	@Override
	public CTAttachedFactory addFactory(String string) {
		CTFactoryImpl childfactory = new CTFactoryImpl(client);
		childfactory.setBoundingBox(getBoundingBox().getA().mult(0.5f), getBoundingBox().getB().mult(0.5f));
		return addFactory(string, childfactory);
	}

	@Override
	public CTAttachedFactory addFactory(final String factoryname, final CTFactory factory) {
		CTAttachedFactory cfactory = new CTAttachedFactory(factory);
		this.getFactoryMap().put(factoryname, cfactory);
		return cfactory;
	}

	@Override
	public CTAttachedFactory getFactory(final String name) {
		return getFactoryMap().get(name);
	}

	@Override
	public Set<String> getFactories() {
		return new HashSet<>(getFactoryMap().keySet());
	}

	private Map<String, CTAttachedFactory> getFactoryMap() {
		if (factories == null && bean != null) {
			factories = new HashMap<String, CTAttachedFactory>();

			List<WObject> bcfs = getContent().getObjectList("factories");
			for (WObject bchildfactory : bcfs) {
				CTFactoryImpl f = new CTFactoryImpl(this.client);

				String cfname = bchildfactory.getValue("name");
				String cfid = bchildfactory.getValue("id");
				String bookmark = bchildfactory.getValue("bookmark");

				if (cfid == null) {
					cfid = client.getPublished(bookmark);
				}

				if (f.load(new MStringID(cfid))) {
					CTAttachedFactory cf = addFactory(cfname, f);
					cf.setBookmark(bookmark);
					cf.set(bchildfactory.get("orientation"));
				} else {
					WLogger.getLogger(this).error("failed to load childfactory " + bchildfactory.toYaml());
				}
			}
		}

		return factories;
	}

	public CTTool getTool(String name) {
		return getEnvironment().getTool(name);
	}

	public CTEnvironment getEnvironment() {
		if (env == null && bean != null) {
			env = new CTEnvironmentImpl(client, getContent().getIDValue(VALUENAME_ENVIRONMENTID));
		}
		return env;
	}

	@Override
	public void setModel(CTBinaryModel model) {
		this.model = model;
	}

	@Override
	public void setToolUserSpawnLocation(Vector3f spawnlocation) {
		this.tooluserspawnlocation = spawnlocation;
	}

	@Override
	public Vector3f getToolUserSpawnLocation() {
		if (tooluserspawnlocation != null) {
			return tooluserspawnlocation;
		} else {
			return null;
		}
	}
}
