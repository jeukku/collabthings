package org.collabthings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.collabthings.factory.CTObjectFactory;
import org.collabthings.model.CTObject;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.common.WPreferences;
import waazdoh.common.client.WServiceClient;

public interface CTClient {
	public final String VERSION = "0.0.3";
	public String LOWEST_ACCEPTED_VERSION = "0.0.3";
	public String CHARSET = "UTF-8";

	public String JAVASCRIPT_FORBIDDENWORDS = "ct.javascript.forbiddenwords";
	public String PREFERENCES_OPENSCADPATH = "software.openscad.path";

	public String ERROR_OPENSCADFAILED = "error_openscadfailed";

	CTObjectFactory getObjectFactory();

	String getVersion();

	String getPrefix();

	WClient getClient();

	BinarySource getBinarySource();

	void stop();

	boolean isRunning();

	WPreferences getPreferences();

	String getGlobalSetting(String name);

	WServiceClient getService();

	CTStorage getStorage();

	void publish(String string, CTObject o);

	String getPublished(String username, String string);

	String getPublished(String value);

	void errorEvent(String error, Exception e);

	CTBookmarks getBookmarks();

	void addErrorListener(CTErrorListener listener);

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

}
