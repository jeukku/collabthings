package org.collabthings.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTListener;
import org.collabthings.model.CTBoundingBox;
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
	//
	private ServiceObject o;
	private String name = "part";
	private String shortname = "part";

	CTClient env;

	private List<CTSubPart> subparts = new ArrayList<>();
	private CTBoundingBox boundingbox = new CTBoundingBox(new Vector3f(), new Vector3f());
	private CTMaterial material = new CTMaterialImpl();

	private CTModel model;
	private CTPartBuilder builder;
	private WObject storedobject;
	private List<CTListener> listeners = new ArrayList<>();
	private boolean ready;

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
			}

			if (builder != null) {
				b.addValue(VALUENAME_BUILDERID, builder.getID().toString());
			}

			b.add("material", material.getBean());

			if (getBoundingBox() != null) {
				b.add(CTBoundingBox.BEAN_NAME, getBoundingBox().getBean());
			}
			//
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
	public synchronized boolean parse(WObject bean) {
		LLog.getLogger(this).info("Loading " + bean);

		ready = false;

		bean = bean.get("content");
		if (bean != null) {
			setName(bean.getValue(VALUENAME_NAME));
			setShortname(bean.getValue(VALUENAME_SHORTNAME));

			parseModel(bean.get("model"));
			parseBuilder(bean.getValue(VALUENAME_BUILDERID));

			material = new CTMaterialImpl(bean.get("material"));

			WObject beanboundingbox = bean.get(CTBoundingBox.BEAN_NAME);
			if (beanboundingbox != null) {
				boundingbox = new CTBoundingBox(beanboundingbox);
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
		} else {
			LLog.getLogger(this).info("No content info " + bean);
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
			} else {
				MStringID modelid = data.getIDValue(VALUENAME_MODELID);
				CT3DModelImpl m = new CT3DModelImpl(env);
				m.load(modelid);
				model = m;
			}

			if (model != null) {
				model.addChangeListener(() -> changed());
			}
		}
	}

	private synchronized void addPart(CTSubPartImpl subpart) {
		getLog().info("addPart " + subpart);
		subparts.add(subpart);
		subpart.addChangeListener(() -> changed());
		changed();
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
		changed();
	}

	public String getName() {
		return name;
	}

	public void setName(final String nname) {
		this.name = nname;
		changed();
	}

	@Override
	public CTMaterial getMaterial() {
		return material;
	}

	@Override
	public void setBoundingBox(Vector3f a, Vector3f b) {
		boundingbox = new CTBoundingBox(a, b);
		changed();
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
		changed();
		return builder;
	}

	@Override
	public boolean isReady() {
		if (!ready || getModel() != null && !getModel().isReady()) {
			return false;
		}

		return true;
	}

	public void save() {
		if (getServiceObject().hasChanged()) {
			changed();

			if (model != null) {
				model.save();
			}

			if (builder != null) {
				builder.save();
			}

			subparts.parallelStream().forEach(subpart -> {
				subpart.save();
			});

			getServiceObject().save();
		}
	}

	public void publish() {
		if (getServiceObject().hasChanged()) {
			save();

			if (model != null) {
				model.publish();
			}

			if (builder != null) {
				builder.publish();
			}

			subparts.parallelStream().forEach(subpart -> {
				subpart.publish();
			});

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
		changed();
		return m;
	}

	public synchronized CTSubPart newSubPart() {
		CTSubPartImpl spart = new CTSubPartImpl(this, env);
		getLog().info("New subpart " + spart);
		addPart(spart);
		return spart;
	}

	@Override
	public void removeSubPart(CTSubPart subpart) {
		subparts.remove(subpart);
		changed();
	}

	@Override
	public synchronized List<CTSubPart> getSubParts() {
		return new ArrayList<>(subparts);
	}

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
	public void resetModel() {
		model = null;
	}

	@Override
	public CTOpenSCAD newSCAD() {
		CTOpenSCADImpl scad = new CTOpenSCADImpl(env);
		model = scad;
		model.addChangeListener(() -> changed());

		changed();
		return scad;
	}

	private void changed() {
		o.modified();
		this.storedobject = null;
		listeners.stream().forEach((e) -> e.event());
	}

	@Override
	public void addChangeListener(CTListener listener) {
		listeners.add(listener);
	}

	public void setReady() {
		ready = true;
	}
}
