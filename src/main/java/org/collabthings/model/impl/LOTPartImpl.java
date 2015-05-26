package org.collabthings.model.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.LLog;
import org.collabthings.LOTClient;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTMaterial;
import org.collabthings.model.LOTModel;
import org.collabthings.model.LOTOpenSCAD;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTSubPart;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WData;

public final class LOTPartImpl implements ServiceObjectData, LOTPart {
	private static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "id";
	//
	private ServiceObject o;
	private String name = "part";

	LOTClient env;

	private List<LOTSubPart> subparts = new LinkedList<>();
	private LOTBoundingBox boundingbox;
	private LOTMaterial material = new LOTMaterialImpl();

	private LOTModel model;

	public LOTPartImpl(final LOTClient nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
	}

	@Override
	public String toString() {
		return "P[" + name + "][sub:" + subparts.size() + "]";
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public WData getBean() {
		WData b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());

		if (model != null) {
			WData md = b.add("model");
			md.addValue("id", model.getID());
			md.addValue("type", model.getModelType());
		}

		b.add(material.getBean());

		if (getBoundingBox() != null) {
			b.add(getBoundingBox().getBean());
		}
		//
		WData bparts = getSubPartsBean();
		//
		b.add(bparts);

		//
		return b;
	}

	private synchronized WData getSubPartsBean() {
		WData bparts = new WData("parts");
		for (LOTSubPart part : getSubParts()) {
			WData bpart = bparts.add("part");
			((LOTSubPartImpl) part).getBean(bpart);
		}
		return bparts;
	}

	@Override
	public boolean parseBean(WData bean) {
		setName(bean.getValue(VALUENAME_NAME));

		parseModel(bean.get("model"));

		material = new LOTMaterialImpl(bean.get("material"));

		WData beanboundingbox = bean.get(LOTBoundingBox.BEAN_NAME);
		if (beanboundingbox != null) {
			boundingbox = new LOTBoundingBox(beanboundingbox);
		}
		//
		WData bparts = bean.get("parts");
		if (bparts != null) {
			for (WData bpart : bparts.getChildren()) {
				LOTSubPartImpl subpart = new LOTSubPartImpl(this, env);
				subpart.parse(bpart);
				addPart(subpart);
			}
		}
		//
		return getName() != null;
	}

	private void parseModel(WData data) {
		if (data != null) {
			String type = data.getValue("type");
			if (LOTOpenSCAD.TYPE.equals(type)) {
				MStringID scadid = data.getIDValue(VALUENAME_MODELID);
				LOTOpenSCADImpl nscad = new LOTOpenSCADImpl(this.env);
				nscad.load(scadid);
				model = nscad;
			} else {
				MStringID modelid = data.getIDValue(VALUENAME_MODELID);
				LOT3DModelImpl m = new LOT3DModelImpl(env);
				m.load(modelid);
				model = m;
			}
		}
	}

	private synchronized void addPart(LOTSubPartImpl subpart) {
		getLog().info("addPart " + subpart);
		subparts.add(subpart);
	}

	private LLog getLog() {
		return LLog.getLogger(this);
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	public String getName() {
		return name;
	}

	public void setName(final String nname) {
		this.name = nname;
	}

	@Override
	public LOTMaterial getMaterial() {
		return material;
	}

	@Override
	public void setBoundingBox(LVector a, LVector b) {
		boundingbox = new LOTBoundingBox(a, b);
	}

	@Override
	public LOTBoundingBox getBoundingBox() {
		return boundingbox;
	}

	@Override
	public boolean isReady() {
		if (getModel() != null && !getModel().isReady()) {
			return false;
		}

		return true;
	}

	public void save() {
		if (model != null) {
			model.save();
		}

		for (LOTSubPart subpart : this.subparts) {
			subpart.getPart().save();
		}

		getServiceObject().save();
	}

	public void publish() {
		save();

		if (model != null) {
			model.publish();
		}

		for (LOTSubPart subpart : this.subparts) {
			subpart.getPart().publish();
		}

		getServiceObject().publish();
	}

	@Override
	public LOTModel getModel() {
		return model;
	}

	public LOT3DModelImpl newBinaryModel() {
		LOT3DModelImpl m = new LOT3DModelImpl(env);
		model = m;
		return m;
	}

	public synchronized LOTSubPart newSubPart() {
		LOTSubPartImpl spart = new LOTSubPartImpl(this, env);
		subparts.add(spart);
		getLog().info("New subpart " + spart);
		return spart;
	}

	@Override
	public synchronized List<LOTSubPart> getSubParts() {
		return new LinkedList<>(subparts);
	}

	public boolean importModel(File file) {
		return getModel().importModel(file);
	}

	@Override
	public synchronized boolean isAnEqualPart(LOTPart p) {
		if (p instanceof LOTPartImpl) {
			LOTPartImpl impl = (LOTPartImpl) p;
			WData thisb = this.getSubPartsBean();
			WData thatb = impl.getSubPartsBean();
			return thisb.equals(thatb);
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

	public synchronized void destroy() {
		this.boundingbox = null;
		this.subparts.clear();
		this.o = null;
	}

	@Override
	public LOTOpenSCAD newSCAD() {
		LOTOpenSCADImpl scad = new LOTOpenSCADImpl(env);
		model = scad;
		return scad;
	}
}
