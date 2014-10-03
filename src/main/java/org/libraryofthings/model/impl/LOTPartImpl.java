package org.libraryofthings.model.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTSubPart;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.client.model.MID;
import waazdoh.util.MStringID;

public final class LOTPartImpl implements ServiceObjectData, LOTPart {
	private static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "model3did";
	//
	private ServiceObject o;
	private String name = "part";
	private LOT3DModelImpl model;
	LOTClient env;

	private List<LOTSubPart> subparts = new LinkedList<>();
	private LOTBoundingBox boundingbox;

	public LOTPartImpl(final LOTClient nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
	}

	@Override
	public String toString() {
		return "P[" + name + "][sub:" + subparts.size() + "][" + getID() + "]";
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		b.addValue(VALUENAME_MODELID, getModel().getID());
		if (getBoundingBox() != null) {
			b.add(getBoundingBox().getBean());
		}
		//
		JBean bparts = getSubPartsBean();
		//
		b.add(bparts);

		//
		return b;
	}

	private synchronized JBean getSubPartsBean() {
		JBean bparts = new JBean("parts");
		for (LOTSubPart part : getSubParts()) {
			JBean bpart = bparts.add("part");
			((LOTSubPartImpl) part).getBean(bpart);
		}
		return bparts;
	}

	@Override
	public boolean parseBean(JBean bean) {
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		model = new LOT3DModelImpl(env);
		model.load(modelid);
		//
		JBean beanboundingbox = bean.get(LOTBoundingBox.BEAN_NAME);
		if (beanboundingbox != null) {
			boundingbox = new LOTBoundingBox(beanboundingbox);
		}
		//
		JBean bparts = bean.get("parts");
		for (JBean bpart : bparts.getChildren()) {
			LOTSubPartImpl subpart = new LOTSubPartImpl(this, env);
			subpart.parse(bpart);
			addPart(subpart);
		}
		//
		return getName() != null;
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
	public void setBoundingBox(LVector a, LVector b) {
		boundingbox = new LOTBoundingBox(a, b);
	}

	@Override
	public LOTBoundingBox getBoundingBox() {
		return boundingbox;
	}

	@Override
	public boolean isReady() {
		if (!getModel().isReady()) {
			return false;
		}

		return true;
	}

	public void save() {
		getModel().save();

		for (LOTSubPart subpart : this.subparts) {
			subpart.getPart().save();
		}

		getServiceObject().save();
	}

	public void publish() {
		getModel().publish();
		//
		for (LOTSubPart subpart : this.subparts) {
			subpart.getPart().publish();
		}

		getServiceObject().publish();
	}

	public LOT3DModelImpl getModel() {
		if (model == null) {
			newModel();
		}
		return model;
	}

	public void newModel() {
		model = new LOT3DModelImpl(env);
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
			JBean thisb = this.getSubPartsBean();
			JBean thatb = impl.getSubPartsBean();
			return thisb.equals(thatb);
		} else {
			return false;
		}
	}

	@Override
	public synchronized MID getID() {
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
}
