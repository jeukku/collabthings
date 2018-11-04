package org.collabthings.model.impl;

import org.collabthings.CTClient;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTConnector;

import collabthings.core.ServiceObject;
import collabthings.core.ServiceObjectData;
import collabthings.datamodel.WObject;
import collabthings.datamodel.WObjectID;
import collabthings.datamodel.WStringID;

public class CTConnectorImpl implements CTConnector, ServiceObjectData {

	private static final String BEANNAME = "connector";
	private static final String VALUENAME_APP = "app";
	private CTClient client;
	private ServiceObject o;

	private CTApplication app;

	public CTConnectorImpl(CTClient client) {
		this.client = client;
		app = new CTApplicationImpl(client);
		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
	}

	public CTConnectorImpl(CTClient client2, WStringID connectorid) {
		this.client = client2;
		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
		o.load(connectorid);
	}

	@Override
	public WObject getObject() {
		WObject org = o.getBean();
		org.addValue(VALUENAME_APP, getApplication().getID());
		return org;
	}

	@Override
	public boolean parse(WObject bean) {
		app = client.getObjectFactory().getApplication(bean.getIDValue(VALUENAME_APP));
		return true;
	}

	@Override
	public CTApplication getApplication() {
		if (app == null) {
			app = new CTApplicationImpl(client);
		}
		return app;
	}

	@Override
	public WObjectID getID() {
		return o.getID();
	}

	@Override
	public void save() {
		getApplication().save();
		o.save();	
	}
	
	@Override
	public void publish() {
		save();
		getApplication().publish();
		o.publish();
	}
}
