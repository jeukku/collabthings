package org.libraryofthings;

import org.libraryofthings.model.LOTObjectFactory;

import waazdoh.client.WClient;
import waazdoh.client.binaries.MBinarySource;
import waazdoh.util.MPreferences;

public interface LOTClient {
	public static final String VERSION = "0.0.1";

	LOTObjectFactory getObjectFactory();

	String getVersion();

	String getPrefix();

	WClient getClient();

	MBinarySource getBinarySource();

	void stop();

	MPreferences getPreferences();
}
