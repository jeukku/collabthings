package org.libraryofthings;

import org.libraryofthings.model.LOTObject;
import org.libraryofthings.model.LOTObjectFactory;
import org.libraryofthings.model.LOTRunEnvironmentBuilder;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.common.WPreferences;
import waazdoh.common.client.ServiceClient;

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

	ServiceClient getService();

	LOTStorage getStorage();

	void publish(String string, LOTObject o);

}
