package org.libraryofthings;

import org.libraryofthings.model.LOTObjectFactory;

import waazdoh.client.WClient;
import waazdoh.client.binaries.BinarySource;
import waazdoh.client.model.CMService;
import waazdoh.util.MPreferences;

public interface LOTClient {
	public static final String VERSION = "0.0.1";
	public static final String JAVASCRIPT_FORBIDDENWORDS = "lot.javascript.forbiddenwords";

	LOTObjectFactory getObjectFactory();

	String getVersion();

	String getPrefix();

	WClient getClient();

	BinarySource getBinarySource();

	void stop();

	MPreferences getPreferences();

	String getGlobalSetting(String name);

	CMService getService();

	LOTStorage getStorage();

}
