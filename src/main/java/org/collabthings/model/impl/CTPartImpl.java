package org.collabthings.model.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.math.LVector;
import org.collabthings.model.CTBoundingBox;
import org.collabthings.model.CTMaterial;
import org.collabthings.model.CTModel;
import org.collabthings.model.CTOpenSCAD;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTSubPart;
import org.collabthings.util.LLog;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.common.MStringID;
import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public final class CTPartImpl implements ServiceObjectData, CTPart {
	private static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "id";
	//
	private ServiceObject o;
	private String name = "part";

	CTClient env;

	private List<CTSubPart> subparts = new LinkedList<>();
	private CTBoundingBox boundingbox = new CTBoundingBox(new LVector(), new LVector());
	private CTMaterial material = new CTMaterialImpl();

	private CTModel model;

	public CTPartImpl(final CTClient nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this, nenv.getVersion(), nenv.getPrefix());
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
		WObject org = o.getBean();
		WObject b = org.add("content");
		b.addValue(VALUENAME_NAME, getName());

		if (model != null) {
			WObject md = b.add("model");
			md.addValue("id", model.getID());
			md.addValue("type", model.getModelType());
		}

		b.add("material", material.getBean());

		if (getBoundingBox() != null) {
			b.add(CTBoundingBox.BEAN_NAME, getBoundingBox().getBean());
		}
		//
		addSubParts(b);

		//
		return org;
	}

	private synchronized void addSubParts(WObject b) {
		List<WObject> list = new LinkedList<>();
		for (CTSubPart part : getSubParts()) {
			WObject bpart = new WObject("part");
			((CTSubPartImpl) part).getBean(bpart);
			b.addToList("parts", bpart);
		}
	}

	@Override
	public boolean parse(WObject bean) {
		LLog.getLogger(this).info("Loading " + bean);

		bean = bean.get("content");
		if (bean != null) {
			setName(bean.getValue(VALUENAME_NAME));

			parseModel(bean.get("model"));

			material = new CTMaterialImpl(bean.get("material"));

			WObject beanboundingbox = bean.get(CTBoundingBox.BEAN_NAME);
			if (beanboundingbox != null) {
				boundingbox = new CTBoundingBox(beanboundingbox);
			}
			//
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
				return true;
			} else {
				LLog.getLogger(this).info("Loading failed. Name null. " + bean);
				return false;
			}
		} else {
			LLog.getLogger(this).info("No content info " + bean);
			return false;
		}
	}

	private void parseModel(WObject data) {
		if (data != null) {
			String type = data.getValue("type");
			if (CTModel.SCAD.equals(type)) {
				MStringID scadid = data.getIDValue(VALUENAME_MODELID);
				CTOpenSCAD nscad = this.env.getObjectFactory().getOpenScad(scadid);
				model = nscad;
			} else {
				MStringID modelid = data.getIDValue(VALUENAME_MODELID);
				CT3DModelImpl m = new CT3DModelImpl(env);
				m.load(modelid);
				model = m;
			}
		}
	}

	private synchronized void addPart(CTSubPartImpl subpart) {
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
	public CTMaterial getMaterial() {
		return material;
	}

	@Override
	public void setBoundingBox(LVector a, LVector b) {
		boundingbox = new CTBoundingBox(a, b);
	}

	@Override
	public CTBoundingBox getBoundingBox() {
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
		if (getServiceObject().hasChanged()) {
			if (model != null) {
				model.save();
			}

			for (CTSubPart subpart : this.subparts) {
				subpart.save();
			}

			getServiceObject().save();
		}
	}

	public void publish() {
		if (getServiceObject().hasChanged()) {
			save();

			if (model != null) {
				model.publish();
			}

			for (CTSubPart subpart : this.subparts) {
				subpart.publish();
			}

			getServiceObject().publish();

			this.env.publish(getName(), this);
		}
	}

	@Override
	public CTModel getModel() {
		return model;
	}

	public CT3DModelImpl newBinaryModel() {
		CT3DModelImpl m = new CT3DModelImpl(env);
		model = m;
		return m;
	}

	public synchronized CTSubPart newSubPart() {
		CTSubPartImpl spart = new CTSubPartImpl(this, env);
		subparts.add(spart);
		getLog().info("New subpart " + spart);
		return spart;
	}

	@Override
	public synchronized List<CTSubPart> getSubParts() {
		return new LinkedList<>(subparts);
	}

	public boolean importModel(File file) {
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
	public CTOpenSCAD newSCAD() {
		CTOpenSCADImpl scad = new CTOpenSCADImpl(env);
		model = scad;
		return scad;
	}
}
