package org.libraryofthings.model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.libraryofthings.LOTClient;
import org.xml.sax.SAXException;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.client.model.JBean;
import waazdoh.util.MStringID;

public final class LOTPart implements ServiceObjectData, LOTObject {
	private static final String BEANNAME = "part";
	private static final String VALUENAME_NAME = "name";
	private static final String VALUENAME_MODELID = "model3did";
	//
	private ServiceObject o;
	private String name = "part";
	private LOT3DModel model;
	LOTClient env;

	private List<LOTSubPart> subparts = new LinkedList<LOTSubPart>();

	public LOTPart(final LOTClient nenv) {
		this.env = nenv;
		o = new ServiceObject(BEANNAME, nenv.getClient(), this,
				nenv.getVersion(), nenv.getPrefix());
	}

	public boolean load(MStringID id) {
		return o.load(id);
	}

	@Override
	public JBean getBean() {
		JBean b = o.getBean();
		b.addValue(VALUENAME_NAME, getName());
		b.addValue(VALUENAME_MODELID, getModel().getServiceObject().getID());
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
		model = new LOT3DModel(env);
		model.load(modelid);
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

	public void setName(final String nname) {
		this.name = nname;
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

	public LOT3DModel getModel() {
		if (model == null) {
			newModel();
		}
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

	public LOTSubPart addSubPart(LOTSubPart part) {
		LOTSubPart subpart = newSubPart();
		subpart.setPart(part.getPart());
		subpart.setOrientation(part.getLocation(), part.getNormal());
		//
		return subpart;
	}

	public boolean importModel(File file) {
		return getModel().importModel(file);
	}
}
