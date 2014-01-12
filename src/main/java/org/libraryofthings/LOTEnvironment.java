package org.libraryofthings;

import waazdoh.client.MBinarySource;
import waazdoh.client.WClient;
import waazdoh.cutils.MPreferences;
import waazdoh.service.CMService;

public class LOTEnvironment {
	public static final String VERSION = "0.0.1";
	//
	private WClient client;

	public LOTEnvironment(MPreferences p, MBinarySource binarysource,
			CMService service) {
		client = new WClient(p, binarysource, service);
	}

	public WClient getClient() {
		return client;
	}
}
