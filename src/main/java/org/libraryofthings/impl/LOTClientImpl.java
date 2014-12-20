package org.libraryofthings.impl;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTStorage;
import org.libraryofthings.model.LOTObjectFactory;
import org.libraryofthings.model.impl.LOTObjectFactoryImpl;

import waazdoh.client.WClient;
import waazdoh.client.binaries.BinarySource;
import waazdoh.client.model.CMService;
import waazdoh.util.MPreferences;

public final class LOTClientImpl implements LOTClient {
	private final String prefix = "LOT";
	//
	private final WClient client;
	private final LOTStorage storage;
	private final LOTObjectFactory factory;
	private LLog log = LLog.getLogger(this);

	public LOTClientImpl(MPreferences p, BinarySource binarysource,
			CMService service) {
		client = new WClient(p, binarysource, service);
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
		return prefix;
	}

	public MPreferences getPreferences() {
		return client.getPreferences();
	}

	public void stop() {
		getClient().stop();
	}

	@Override
	public String getGlobalSetting(String name) {
		return client
				.readStorageArea("/public/" + prefix + "/settings/" + name);
	}

	@Override
	public String toString() {
		return "" + this.client;
	}

	@Override
	public CMService getService() {
		return getClient().getService();
	}
}
