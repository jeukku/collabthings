package org.libraryofthings.model;

import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LOTEnvironment;

import waazdoh.client.Binary;
import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.cutils.MStringID;
import waazdoh.cutils.xml.JBean;

public class LOTPart implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "value";
	private static final String VALUENAME_MODELID = "model3did";
	//
	private ServiceObject o;
	private String name;
	private LOT3DModel model;
	LOTEnvironment env;

	private List<LOTSubPart> subparts = new LinkedList<LOTSubPart>();

	public LOTPart(LOTEnvironment env) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
	}

	public LOTPart(LOTEnvironment env, MStringID id) {
		this.env = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.version);
		o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		if (model != null) {
			b.addValue(VALUENAME_MODELID, model.getServiceObject().getID());
		}
		//
		JBean bparts = b.add("parts");
		for (LOTSubPart part : subparts) {
			JBean bpart = bparts.add("part");
			part.getBean(bpart);
		}
		//
		return b;
	}

	@Override
	public boolean parseBean(JBean bean) {
		setName(bean.getValue(VALUENAME_NAME));
		MStringID modelid = bean.getIDValue(VALUENAME_MODELID);
		if (modelid != null) {
			model = new LOT3DModel(env, modelid);
		}
		//
		JBean bparts = bean.get("parts");
		for (JBean bpart : bparts.getChildren()) {
			LOTSubPart subpart = new LOTSubPart(this, env);
			subpart.parse(bpart);
			addPart(subpart);
		}
		//
		return getName() != null;
	}

	private void addPart(LOTSubPart subpart) {
		subparts.add(subpart);
	}

	public ServiceObject getServiceObject() {
		return o;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isReady() {
		if (model != null && !model.isReady()) {
			return false;
		}

		return true;
	}

	public void save() {
		if (model != null) {
			model.save();
		}

		getServiceObject().save();
	}

	public void publish() {
		if (model != null) {
			model.publish();
		}

		getServiceObject().publish();
	}

	public LOT3DModel getModel() {
		return model;
	}

	public void newModel() {
		model = new LOT3DModel(env);
	}

	public LOTSubPart newSubPart() {
		LOTSubPart spart = new LOTSubPart(this, env);
		addPart(spart);
		return spart;
	}

	public List<LOTSubPart> getSubParts() {
		return subparts;
	}
}
