package org.libraryofthings;

import waazdoh.client.MBinarySource;
import waazdoh.client.WClient;
import waazdoh.cutils.MPreferences;
import waazdoh.service.CMService;

public class LOTClient {
	private WClient client;

	public LOTClient(MPreferences p, MBinarySource binarysource,
			CMService service) {
		client = new WClient(p, binarysource, service);
	}

	public WClient getWClient() {
		return client;
	}
}
