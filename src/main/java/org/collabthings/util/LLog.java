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

package org.collabthings.util;

import org.apache.log4j.Logger;

public class LLog {
	private Logger log;
	private String info = "";
	private int maxinfolength = 40;

	LLog(Object o) {
		log = Logger.getLogger("" + o);
		setInfo("" + o);
	}

	public void info(String string) {
		log.info(getLine(string));
	}

	private String getLine(String string) {
		return "[" + info + "] " + string;
	}

	public void error(Object o, String sourceMethod, Throwable e1) {
		log.warn("ERROR in " + o + " " + sourceMethod + " throwable:" + e1);
		log.error("" + o, e1);
	}

	public static LLog getLogger(Object o) {
		return new LLog(o);
	}

	public void fine(String string) {
		log.debug(getLine(string));
	}

	public LLog instance(Object o) {
		return LLog.getLogger(o);
	}

	public void setInfo(final String ninfo) {
		this.info = ninfo;
		if (info.length() > maxinfolength) {
			info = info.substring(0, maxinfolength);
		} else {
			while (info.length() < maxinfolength) {
				info = info + " ";
			}
		}
	}

	public void warning(String string) {
		log.warn(getLine(string));
	}

	public void error(String string) {
		log.error(string);
	}
}
