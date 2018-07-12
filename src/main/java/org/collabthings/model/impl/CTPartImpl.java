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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.collabthings.model.CTVectorGroup;
import org.collabthings.model.CTViewingProperties;
import org.collabthings.util.LLog;

import com.jme3.math.Vector3f;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.datamodel.WObject;
import waazdoh.datamodel.WObjectID;
import waazdoh.datamodel.WStringID;

public final class CTPartImpl implements ServiceObjectData, CTPart {
	public static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "id";
	private static final String VALUENAME_MODEL = "model";
	private static final String VALUENAME_SHORTNAME = "shortname";
	private static final String VALUENAME_BUILDERID = "builder";
	private static final String VALUENAME_RESOURCEUSAGE = "resources";
	private static final String VALUENAME_VIEWINGPROPERTIES = "view";
	private static final String VALUENAME_VECTORGROUPS = "vgs";
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
	private CTViewingProperties viewingproperties;
	private Map<String, CTVectorGroup> vectorgroups;

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
	public boolean load(WStringID id) {
		return o.load(id);
	}

	@Override
	public synchronized WObject getObject() {
		if (hasChanged()) {
			WObject org = o.getBean();
			WObject b = org.add("content");
			b.addValue(VALUENAME_NAME, getName());
			b.addValue(VALUENAME_SHORTNAME, getShortname());

			if (model != null) {
				WObject md = b.add(VALUENAME_MODEL);
				md.addValue("id", model.getID());
				md.addValue("type", model.getModelType());
			} else if (modeldata != null) {
				WObject md = b.add(VALUENAME_MODEL);
				WStringID scadid = modeldata.getIDValue(VALUENAME_MODELID);
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

			if (viewingproperties != null) {
				viewingproperties.getObject(b.add(VALUENAME_VIEWINGPROPERTIES));
			}

			addSubParts(b);
			addVectorGroups(b);

			storedobject = org;
		}

		return storedobject;
	}

	@Override
	public CTViewingProperties getViewingProperties() {
		if (viewingproperties == null) {
			viewingproperties = new CTViewingPropertiesImpl();
			List<CTSubPart> sps = getSubParts();
			Vector3f lookat = new Vector3f();
			for (CTSubPart cp : sps) {
				lookat.addLocal(cp.getLocation());
			}
			if (!sps.isEmpty()) {
				lookat.multLocal(1.0f / sps.size());
			}
			viewingproperties.setLookAt(lookat);
		}

		return viewingproperties;
	}

	private synchronized void addSubParts(WObject b) {
		for (CTSubPart part : getSubParts()) {
			WObject bpart = new WObject("part");
			((CTSubPartImpl) part).getBean(bpart);
			b.addToList("parts", bpart);
		}
	}

	private synchronized void addVectorGroups(WObject b) {
		if (vectorgroups != null) {
			WObject bvg = b.add(VALUENAME_VECTORGROUPS);
			for (String name : vectorgroups.keySet()) {
				CTVectorGroup vg = vectorgroups.get(name);
				vg.addTo(bvg.add(name));
			}
		}
	}

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

		modeldata = bean.get(VALUENAME_MODEL);

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

		WObject oviewingproperties = bean.get(VALUENAME_VIEWINGPROPERTIES);
		if (oviewingproperties != null) {
			viewingproperties = new CTViewingPropertiesImpl(oviewingproperties);
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

		WObject bvgs = bean.get(VALUENAME_VECTORGROUPS);
		if (bvgs != null) {
			vectorgroups = new HashMap<>();
			for (String vgname : bvgs.getChildren()) {
				addVectorGroup(vgname).parse(bvgs.get(vgname));
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
			builder = this.env.getObjectFactory().getPartBuilder(new WStringID(pbid));
		}
	}

	private void parseModel(WObject data) {
		if (data != null) {
			String type = data.getValue("type");
			WStringID nmodelid = data.getIDValue(VALUENAME_MODELID);
			if (CTConstants.MODELTYPE_SCAD.equals(type)) {
				CTOpenSCAD nscad = this.env.getObjectFactory().getOpenScad(nmodelid);
				model = nscad;
			} else if (CTConstants.MODELTYPE_HEIGHTMAP.equals(type)) {
				CTHeightmap nhm = this.env.getObjectFactory().getHeightmap(nmodelid);
				model = nhm;
			} else {
				CT3DModelImpl m = new CT3DModelImpl(env);
				m.load(nmodelid);
				model = m;
			}

			if (model != null) {
				if (!model.getID().getStringID().equals(nmodelid)) {
					setChanged();
				}
				model.addChangeListener(this::changed);
			}
		}
	}

	@Override
	public boolean hasChanged() {
		if (storedobject == null) {
			return true;
		}

		for (CTSubPart subpart : subparts) {
			if (subpart.hasPartChanged()) {
				return true;
			}
		}

		return false;
	}

	private void setChanged() {
		this.storedobject = null;
	}

	private synchronized void addPart(CTSubPartImpl subpart) {
		getLog().info("addPart " + subpart);
		subparts.add(subpart);
		subpart.addChangeListener(e -> {
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

			subparts.parallelStream().forEach(CTSubPart::save);

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

			subparts.parallelStream().forEach(CTSubPart::publish);

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
	public synchronized WObjectID getID() {
		if (getServiceObject() != null) {
			return getServiceObject().getID();
		} else {
			return null;
		}
	}

	@Override
	public void resetModel() {
		model = null;
		modeldata = null;
		changed(new CTEvent("model reset"));
	}

	@Override
	public CTHeightmap getHeightmap() {
		if (getModel() != null && getModel().getModelType().equals(CTConstants.MODELTYPE_HEIGHTMAP)) {
			return (CTHeightmap) model;
		} else {
			return null;
		}
	}

	@Override
	public CTHeightmap newHeightmap() {
		CTHeightmapImpl map = new CTHeightmapImpl(env);
		model = map;
		model.addChangeListener(this::changed);

		storedobject = null;
		builder = null;

		changed(new CTEvent("newHM"));

		return map;
	}

	@Override
	public CTOpenSCAD newSCAD() {
		CTOpenSCADImpl scad = new CTOpenSCADImpl(env);
		model = scad;
		model.addChangeListener(this::changed);

		changed(new CTEvent("new SCAD"));
		return scad;
	}

	private void changed(CTEvent e) {
		o.modified();
		this.storedobject = null;

		updateResourceUsage();

		listeners.stream().forEach(l -> l.event(e));
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
	public CTVectorGroup addVectorGroup(String string) {
		if (vectorgroups == null) {
			vectorgroups = new HashMap<>();
		}

		CTVectorGroup vg = getVectorGroup(string);
		if (vg == null) {
			vg = new CTVectorGroup();
			vectorgroups.put(string, vg);
		}

		return vg;
	}

	@Override
	public CTVectorGroup getVectorGroup(String string) {
		if (vectorgroups == null) {
			return null;
		}
		return vectorgroups.get(string);
	}

	@Override
	public void addChangeListener(CTListener listener) {
		listeners.add(listener);
	}

	public void setReady() {
		ready = true;
	}
}
