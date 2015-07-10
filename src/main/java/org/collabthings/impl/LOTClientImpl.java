package org.collabthings.impl;

import org.collabthings.LOTClient;
import org.collabthings.LOTStorage;
import org.collabthings.factory.LOTObjectFactory;
import org.collabthings.factory.impl.LOTObjectFactoryImpl;
import org.collabthings.model.LOTObject;
import org.collabthings.util.LLog;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.common.BeanStorage;
import waazdoh.common.WPreferences;
import waazdoh.common.client.ServiceClient;

public final class LOTClientImpl implements LOTClient {
	private final static String PREFIX = "LOT";
	//
	private final WClient client;
	private final LOTStorage storage;
	private final LOTObjectFactory factory;
	private LLog log = LLog.getLogger(this);

	public LOTClientImpl(WPreferences p, BinarySource binarysource, BeanStorage beanstorage,
			ServiceClient service) {
		client = new WClient(p, binarysource, beanstorage, service);
		this.factory = new LOTObjectFactoryImpl(this);
		this.storage = new LOTStorageImpl(service);
	}

	public WClient getClient() {
		return client;
	}

	public BinarySource getBinarySource() {
		return client.getBinarySource();
	}

	public LOTObjectFactory getObjectFactory() {
		return factory;
	}

	@Override
	public LOTStorage getStorage() {
		return storage;
	}

	public String getVersion() {
		return LOTClientImpl.VERSION;
	}

	public String getPrefix() {
		return PREFIX;
	}

	public WPreferences getPreferences() {
		return client.getPreferences();
	}

	public void stop() {
		getClient().stop();
	}

	@Override
	public String getGlobalSetting(String name) {
		return client.readStorageArea("/public/" + PREFIX + "/settings/" + name);
	}

	@Override
	public String toString() {
		return "" + this.client;
	}

	@Override
	public ServiceClient getService() {
		return getClient().getService();
	}

	public boolean isRunning() {
		if (getClient().isRunning()) {
			return true;
		}

		return false;
	}

	@Override
	public void publish(String string, LOTObject o) {
		String path = "published/" + string;
		path = path.replace("//", "/");

		String id = "" + o.getID();

		log.info("writing " + path + " value:" + id);

		client.getService().getStorageArea().write(path, id);
	}
}
