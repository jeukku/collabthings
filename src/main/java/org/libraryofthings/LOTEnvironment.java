package org.libraryofthings;

import waazdoh.client.MBinarySource;
import waazdoh.client.WClient;
import waazdoh.cutils.MPreferences;
import waazdoh.service.CMService;

public final class LOTEnvironment {
	public static final String version = "0.0.1";
	private final String prefix = "LOT";
	//
	private WClient client;
	private LOTObjectFactory factory;
	private LLog log = LLog.getLogger(this);

	public LOTEnvironment(MPreferences p, MBinarySource binarysource,
			CMService service) {
		client = new WClient(p, binarysource, service);
		this.factory = new LOTObjectFactoryImpl(this);
		log.info("resources " + getClass().getResource("/").getPath());
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

	public String getVersion() {
		return LOTEnvironment.version;
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
	public String toString() {
		return "" + this.client;
	}
}
