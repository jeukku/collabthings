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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTEvent;
import org.collabthings.CTListener;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTHeightmap;
import org.collabthings.model.CTMaterial;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTSubPart;
import org.collabthings.util.LLog;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public final class CTPartImpl implements ServiceObjectData, CTPart {
	public static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "id";
	private static final String VALUENAME_SHORTNAME = "shortname";
	private static final String VALUENAME_BUILDERID = "builder";
	private static final String VALUENAME_RESOURCEUSAGE = "resources";
	//
	private ServiceObject o;
	private String name = "part";
	private String shortname = "part";

	private CTClient env;

	private List<CTSubPart> subparts = new ArrayList<>();
	private CTBoundingBox boundingbox = new CTBoundingBox(new Vector3f(), new Vector3f());
	private CTMaterial material = new CTMaterialImpl();

	private CTModel model;
	private CTPartBuilder builder;
	private CTResourceUsage resourceusage;

	private WObject storedobject;
	private List<CTListener> listeners = new ArrayList<>();
	private boolean ready;
	private WObject modeldata;

	private static LLog log = LLog.getLogger("PartImpl");

	public CTPartImpl(final CTClient nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this, nenv.getVersion(), nenv.getPrefix());
	}

	@Override
	public String toString() {
		return "P[" + name + "][sub:" + subparts.size() + "]";
	}

	@Override
	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public synchronized WObject getObject() {
		if (storedobject == null) {
			WObject org = o.getBean();
			WObject b = org.add("content");
			b.addValue(VALUENAME_NAME, getName());
			b.addValue(VALUENAME_SHORTNAME, getShortname());

			if (model != null) {
				WObject md = b.add("model");
				md.addValue("id", model.getID());
				md.addValue("type", model.getModelType());
			} else if (modeldata != null) {
				WObject md = b.add("model");
				MStringID scadid = modeldata.getIDValue(VALUENAME_MODELID);
				md.addValue("id", scadid.toString());
				String type = modeldata.getValue("type");
				md.addValue("type", type);
			}

			if (builder != null) {
				b.addValue(VALUENAME_BUILDERID, builder.getID().toString());
			}

			b.add("material", material.getBean());

			if (getBoundingBox() != null) {
				b.add(CTBoundingBox.BEAN_NAME, getBoundingBox().getBean());
			}

			if (resourceusage != null) {
				resourceusage.getObject(b.add(VALUENAME_RESOURCEUSAGE));
			}

			addSubParts(b);

			storedobject = org;
		}

		return storedobject;
	}

	private synchronized void addSubParts(WObject b) {
		for (CTSubPart part : getSubParts()) {
			WObject bpart = new WObject("part");
			((CTSubPartImpl) part).getBean(bpart);
			b.addToList("parts", bpart);
		}
	};

	@Override
	public synchronized boolean parse(WObject main) {
		LLog.getLogger(this).info("Loading " + main);

		ready = false;

		WObject bean = main.get("content");
		if (bean == null) {
			bean = main;
		}

		name = bean.getValue(VALUENAME_NAME);
		shortname = bean.getValue(VALUENAME_SHORTNAME);

		modeldata = bean.get("model");

		parseBuilder(bean.getValue(VALUENAME_BUILDERID));

		material = new CTMaterialImpl(bean.get("material"));

		WObject beanboundingbox = bean.get(CTBoundingBox.BEAN_NAME);
		if (beanboundingbox != null) {
			boundingbox = new CTBoundingBox(beanboundingbox);
		}

		WObject bresourceusage = bean.get(VALUENAME_RESOURCEUSAGE);
		if (bresourceusage != null) {
			getResourceUsage().parse(bresourceusage);
		}
		//
		subparts.clear();

		List<WObject> parts = bean.getObjectList("parts");
		if (parts != null) {
			for (WObject bpart : parts) {
				CTSubPartImpl subpart = new CTSubPartImpl(this, env);
				subpart.parse(bpart);
				addPart(subpart);
			}
		}
		//
		if (getName() != null) {
			storedobject = null;
			setReady();
			return true;
		} else {
			LLog.getLogger(this).info("Loading failed. Name null. " + bean);
			return false;
		}
	}

	private void parseBuilder(String pbid) {
		if (pbid != null) {
			builder = this.env.getObjectFactory().getPartBuilder(new MStringID(pbid));
		}
	}

	private void parseModel(WObject data) {
		if (data != null) {
			String type = data.getValue("type");
			if (CTModel.SCAD.equals(type)) {
				MStringID scadid = data.getIDValue(VALUENAME_MODELID);
				CTOpenSCAD nscad = this.env.getObjectFactory().getOpenScad(scadid);
				model = nscad;
			} else if (CTModel.HEIGHTMAP.equals(type)) {
				MStringID scadid = data.getIDValue(VALUENAME_MODELID);
				CTHeightmap nhm = this.env.getObjectFactory().getHeightmap(scadid);
				model = nhm;
			} else {
				MStringID modelid = data.getIDValue(VALUENAME_MODELID);
				CT3DModelImpl m = new CT3DModelImpl(env);
				m.load(modelid);
				model = m;
			}

			if (model != null) {
				model.addChangeListener((e) -> changed(e));
			}
		}
	}

	private synchronized void addPart(CTSubPartImpl subpart) {
		getLog().info("addPart " + subpart);
		subparts.add(subpart);
		subpart.addChangeListener((e) -> {
			if (!e.isHandled(this)) {
				e.addHandled(this);
				changed(e);
			}
		});
		changed(new CTEvent("part added"));
	}

	private LLog getLog() {
		return LLog.getLogger(this);
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public String getShortname() {
		return shortname;
	}

	@Override
	public void setShortname(String sname) {
		this.shortname = sname;
		changed(new CTEvent("shortname set"));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override

	public void setName(final String nname) {
		this.name = nname;
		changed(new CTEvent("name set"));
	}

	@Override
	public CTMaterial getMaterial() {
		return material;
	}

	@Override
	public void setBoundingBox(Vector3f a, Vector3f b) {
		boundingbox = new CTBoundingBox(a, b);
		changed(new CTEvent("boundingbox set"));
	}

	@Override
	public CTBoundingBox getBoundingBox() {
		return boundingbox;
	}

	@Override
	public CTPartBuilder getBuilder() {
		return builder;
	}

	@Override
	public CTPartBuilder newBuilder() {
		builder = new CTPartBuilderImpl(env);
		changed(new CTEvent("new builder"));
		return builder;
	}

	@Override
	public boolean isReady() {
		if (!ready || (getModel() != null && !getModel().isReady())) {
			return false;
		}

		return true;
	}

	@Override
	public synchronized void save() {
		if (getServiceObject().hasChanged()) {
			log.info("saving " + this + " " + getID());

			changed(new CTEvent("saving and changed"));

			if (model != null) {
				model.save();
			}

			if (builder != null) {
				builder.save();
			}

			subparts.parallelStream().forEach(subpart -> subpart.save());

			updateResourceUsage();

			getServiceObject().save();
		}
	}

	@Override
	public void publish() {
		if (getServiceObject().hasChanged()) {
			save();

			if (model != null) {
				model.publish();
			}

			if (builder != null) {
				builder.publish();
			}

			subparts.parallelStream().forEach(subpart -> subpart.publish());

			getServiceObject().publish();

			this.env.publish(getName(), this);
		}
	}

	@Override
	public CTModel getModel() {
		if (this.model == null) {
			parseModel(this.modeldata);
		}

		return model;
	}

	@Override
	public CT3DModelImpl newBinaryModel() {
		CT3DModelImpl m = new CT3DModelImpl(env);
		model = m;
		changed(new CTEvent("new binary model"));
		return m;
	}

	@Override
	public synchronized CTSubPart newSubPart() {
		CTSubPartImpl spart = new CTSubPartImpl(this, env);
		getLog().info("New subpart " + spart);
		addPart(spart);
		return spart;
	}

	@Override
	public void removeSubPart(CTSubPart subpart) {
		subparts.remove(subpart);
		changed(new CTEvent("subparts removed"));
	}

	@Override
	public synchronized List<CTSubPart> getSubParts() {
		return new ArrayList<>(subparts);
	}

	@Override
	public boolean importModel(File file) throws IOException {
		return getModel().importModel(file);
	}

	@Override
	public synchronized boolean isAnEqualPart(CTPart p) {
		if (p instanceof CTPartImpl) {
			CTPartImpl that = (CTPartImpl) p;
			WObject thisb = new WObject();
			addSubParts(thisb);
			WObject thatb = new WObject();
			that.addSubParts(thatb);
			thisb.addValue("modifytime", 0);
			thatb.addValue("modifytime", 0);
			if (!thisb.equals(thatb)) {
				LLog.getLogger(this).info("this " + thisb.toYaml());
				LLog.getLogger(this).info("is not equal to " + thatb.toYaml());
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public synchronized ObjectID getID() {
		if (getServiceObject() != null) {
			return getServiceObject().getID();
		} else {
			return null;
		}
	}

	@Override
	public void resetModel() {
		model = null;
		changed(new CTEvent("model reset"));
	}

	@Override
	public CTHeightmap newHeightmap() {
		CTHeightmapImpl map = new CTHeightmapImpl(env);
		model = map;
		model.addChangeListener((e) -> changed(e));

		changed(new CTEvent("newHM"));
		return map;
	}

	@Override
	public CTOpenSCAD newSCAD() {
		CTOpenSCADImpl scad = new CTOpenSCADImpl(env);
		model = scad;
		model.addChangeListener((e) -> changed(e));

		changed(new CTEvent("new SCAD"));
		return scad;
	}

	private void changed(CTEvent e) {
		o.modified();
		this.storedobject = null;

		updateResourceUsage();

		listeners.stream().forEach((l) -> l.event(e));
	}

	@Override
	public void updateResourceUsage() {
		getResourceUsage().updateTotal(subparts);
	}

	@Override
	public CTResourceUsage getResourceUsage() {
		if (resourceusage == null) {
			resourceusage = new CTResourceUsage();
		}
		return resourceusage;
	}

	@Override
	public void addChangeListener(CTListener listener) {
		listeners.add(listener);
	}

	public void setReady() {
		ready = true;
	}
}
