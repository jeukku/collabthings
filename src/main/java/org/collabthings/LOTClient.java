package org.collabthings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.collabthings.factory.LOTObjectFactory;
import org.collabthings.model.LOTObject;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.common.WPreferences;
import waazdoh.common.client.ServiceClient;

public interface LOTClient {
	public static final String VERSION = "0.0.2";
	public final String LOWEST_ACCEPTED_VERSION = "0.0.2";

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

	String getPublished(String username, String string);

	String getPublished(String value);

	public static String getDateTime() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");
		return d.format(date);
	}

	public static boolean checkVersion(String v) {
		if (v != null) {
			String a = getParsedVersionString(v);
			String b = getParsedVersionString(LOWEST_ACCEPTED_VERSION);
			return a.compareTo(b) >= 0;
		} else {
			return false;
		}
	}

	public static String getParsedVersionString(String v) {
		String s = "";
		StringTokenizer vt = new StringTokenizer(v, ".");
		while (vt.hasMoreTokens()) {
			s += String.format("%03d", Integer.parseInt(vt.nextToken()));
		}
		return s;
	}

	LOTBookmarks getBookmarks();
}
