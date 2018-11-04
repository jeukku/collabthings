/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.collabthings.factory.CTObjectFactory;
import org.collabthings.model.CTObject;
import org.collabthings.model.impl.CTConstants;

import collabthings.core.BinarySource;
import collabthings.core.WClient;
import collabthings.core.WServiceClient;
import collabthings.core.utils.WPreferences;

public interface CTClient {
	CTObjectFactory getObjectFactory();

	String getVersion();

	String getPrefix();

	WClient getClient();

	WServiceClient getService();

	void stop();

	boolean isRunning();

	WPreferences getPreferences();

	String getGlobalSetting(String name);

	CTStorage getStorage();

	void publish(String string, CTObject o);

	String getPublished(String username, String string);

	String getPublished(String value);

	void errorEvent(String error, Exception e);

	CTBookmarks getBookmarks();

	BinarySource getBinarySource();

	void addErrorListener(CTErrorListener listener);

	static String getDateTime() {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd");
		return d.format(date);
	}

	static boolean checkVersion(String v) {
		if (v != null) {
			String a = getParsedVersionString(v);
			String b = getParsedVersionString(CTConstants.LOWEST_ACCEPTED_VERSION);
			return a.compareTo(b) >= 0;
		} else {
			return false;
		}
	}

	static String getParsedVersionString(String v) {
		String s = "";
		StringTokenizer vt = new StringTokenizer(v, ".");
		while (vt.hasMoreTokens()) {
			s += String.format("%03d", Integer.parseInt(vt.nextToken()));
		}
		return s;
	}
}
