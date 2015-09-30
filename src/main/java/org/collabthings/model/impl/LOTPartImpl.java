package org.collabthings.model.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.LOTClient;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;
import org.collabthings.model.LOTMaterial;
import org.collabthings.model.LOTModel;
import org.collabthings.model.LOTOpenSCAD;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTSubPart;
import org.collabthings.util.LLog;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

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
	public WObject getObject() {
		WObject b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());

		if (model != null) {
			WObject md = b.add("model");
			md.addValue("id", model.getID());
			md.addValue("type", model.getModelType());
		}

		b.add("material", material.getBean());

		if (getBoundingBox() != null) {
			b.add(LOTBoundingBox.BEAN_NAME, getBoundingBox().getBean());
		}
		//
		addSubParts(b);

		//
		return b;
	}

	private synchronized void addSubParts(WObject b) {
		List<WObject> list = new LinkedList<>();
		for (LOTSubPart part : getSubParts()) {
			WObject bpart = new WObject("part");
			((LOTSubPartImpl) part).getBean(bpart);
			b.addToList("parts", bpart);
		}
	}

	@Override
	public boolean parseBean(WObject bean) {
		setName(bean.getValue(VALUENAME_NAME));

		parseModel(bean.get("model"));

		material = new LOTMaterialImpl(bean.get("material"));

		WObject beanboundingbox = bean.get(LOTBoundingBox.BEAN_NAME);
		if (beanboundingbox != null) {
			boundingbox = new LOTBoundingBox(beanboundingbox);
		}
		//
		List<WObject> parts = bean.getObjectList("parts");
		if (parts != null) {
			for (WObject bpart : parts) {
				LOTSubPartImpl subpart = new LOTSubPartImpl(this, env);
				subpart.parse(bpart);
				addPart(subpart);
			}
		}
		//
		return getName() != null;
	}

	private void parseModel(WObject data) {
		if (data != null) {
			String type = data.getValue("type");
			if (LOTModel.SCAD.equals(type)) {
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
			LOTPartImpl that = (LOTPartImpl) p;
			WObject thisb = new WObject();
			addSubParts(thisb);
			WObject thatb = new WObject();
			that.addSubParts(thatb);
			thisb.addValue("modifytime", 0);
			thatb.addValue("modifytime", 0);
			if (!thisb.equals(thatb)) {
				LLog.getLogger(this).info("this " + thisb.toText());
				LLog.getLogger(this).info("is not equal to " + thatb.toText());
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
