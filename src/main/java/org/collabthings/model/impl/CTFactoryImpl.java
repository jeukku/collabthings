/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.collabthings.CTClient;
import org.collabthings.core.ServiceObject;
import org.collabthings.core.ServiceObjectData;
import org.collabthings.core.utils.WLogger;
import org.collabthings.datamodel.WObject;
import org.collabthings.datamodel.WObjectID;
import org.collabthings.datamodel.WStringID;
import org.collabthings.math.CTMath;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTAttachedFactory;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.CTTool;
import org.collabthings.util.PrintOut;

import com.jme3.math.Vector3f;

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
		factories = new HashMap<>();

		o = new ServiceObject(BEANNAME, client.getClient().getUserID(), client.getClient().getObjects(), this,
				client.getVersion(), client.getPrefix());
		addApplication("start", new CTApplicationImpl(client));
		setBoundingBox(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
	}

	@Override
	public boolean load(WStringID id) {
		env = null;
		name = null;
		factories = null;

		o = new ServiceObject(BEANNAME, client.getClient().getUserID(), client.getClient().getObjects(), this,
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

	public long getCreationTime() {
		return o.getCreationtime();
	}

	@Override
	public WObject getObject() {
		WObject org = o.getBean();
		WObject b = org.add("content");
		b.addValue(VALUENAME_NAME, getName());

		CTEnvironment environment = getEnvironment();
		if (environment != null) {
			b.addValue(VALUENAME_ENVIRONMENTID, environment.getID());
		}

		addVectorBean(b, VALUENAME_SPAWNLOCATION, tooluserspawnlocation);

		b.add(CTBoundingBox.BEAN_NAME, bbox.getBean());

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
		if (c != null) {
			setName(c.getValue(VALUENAME_NAME));

			WObject beansl = c.get(VALUENAME_SPAWNLOCATION);
			if (beansl != null) {
				tooluserspawnlocation = CTMath.parseVector(beansl);
			}

			WObject bbbox = c.get(CTBoundingBox.BEAN_NAME);
			if (bbbox != null) {
				bbox.set(bbbox);
			}
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
	public CTApplication addApplication(String string) {
		return addApplication(string, new CTApplicationImpl(client));
	}

	@Override
	public Set<String> getApplications() {
		CTEnvironment environment = getEnvironment();
		return environment != null ? environment.getApplications() : null;
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public WObjectID getID() {
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
		CTEnvironment environment = getEnvironment();
		if (environment == null || !environment.isReady()) {
			return false;
		}

		return true;
	}

	@Override
	public CTApplication getApplication(String string) {
		CTEnvironment environment = getEnvironment();
		return environment != null ? environment.getApplication(string.toLowerCase()) : null;
	}

	@Override
	public CTApplication addApplication(String applicationname, CTApplication ctApplication) {
		CTEnvironment environment = getEnvironment();
		if (environment != null) {
			environment.addApplication(applicationname.toLowerCase(), ctApplication);
			return ctApplication;
		} else {
			return null;
		}
	}

	@Override
	public void save() {
		if (getModel() != null) {
			model.save();
		}

		CTEnvironment environment = getEnvironment();
		if (environment != null) {
			environment.save();
		}

		getServiceObject().save();

		for (CTAttachedFactory cf : getFactoryMap().values()) {
			cf.getFactory().save();
		}
	}

	public CTBinaryModel getModel() {
		WObject content = getContent();
		if (model == null && bean != null && content != null) {
			WStringID modelid = content.getIDValue(VALUENAME_MODELID);
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

		CTEnvironment environment = getEnvironment();
		if (environment != null) {
			environment.publish();
		}

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
		if (factories == null) {
			factories = new HashMap<>();
		}

		if (!factories.isEmpty()) {
			return factories;
		}

		WObject content = getContent();
		if (bean != null && content != null) {
			List<WObject> bcfs = content.getObjectList("factories");
			for (WObject bchildfactory : bcfs) {
				CTFactoryImpl f = new CTFactoryImpl(this.client);

				String cfname = bchildfactory.getValue("name");
				String cfid = bchildfactory.getValue("id");
				String bookmark = bchildfactory.getValue("bookmark");

				if (cfid == null) {
					cfid = client.getPublished(bookmark);
				}

				if (f.load(new WStringID(cfid))) {
					CTAttachedFactory cf = new CTAttachedFactory(f);
					factories.put(cfname, cf);
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
		CTEnvironment environment = getEnvironment();
		if (environment != null) {
			return environment.getTool(name);
		} else {
			return null;
		}
	}

	@Override
	public CTEnvironment getEnvironment() {
		WObject content = getContent();
		if (env == null && bean != null && content != null) {
			env = new CTEnvironmentImpl(client, content.getIDValue(VALUENAME_ENVIRONMENTID));
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
