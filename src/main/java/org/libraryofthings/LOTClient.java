package org.libraryofthings;

import org.libraryofthings.model.LOTObjectFactory;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.client.service.WService;
import waazdoh.common.WPreferences;

public interface LOTClient {
	public static final String VERSION = "0.0.1";
	public static final String JAVASCRIPT_FORBIDDENWORDS = "lot.javascript.forbiddenwords";

	LOTObjectFactory getObjectFactory();

	String getVersion();

	String getPrefix();

	WClient getClient();

	BinarySource getBinarySource();

	void stop();

	boolean isRunning();

	WPreferences getPreferences();

	String getGlobalSetting(String name);

	WService getService();

	LOTStorage getStorage();

}
