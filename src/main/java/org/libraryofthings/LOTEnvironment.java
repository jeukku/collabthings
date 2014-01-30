package org.libraryofthings;

import waazdoh.client.MBinarySource;
import waazdoh.client.WClient;
import waazdoh.cutils.MPreferences;
import waazdoh.service.CMService;

public final class LOTEnvironment {
	public final String version = "0.0.1";
	public final String prefix = "LOT";
	//
	private WClient client;
	private LOTObjectFactory factory;

	public LOTEnvironment(MPreferences p, MBinarySource binarysource,
			CMService service) {
		client = new WClient(p, binarysource, service);
		this.factory = new LOTObjectFactoryImpl(this);
	}

	public WClient getClient() {
		return client;
	}

	public MBinarySource getBinarySource() {
		return client.getBinarySource();
	}

	public LOTObjectFactory getObjectFactory() {
		return factory;
	}
}
