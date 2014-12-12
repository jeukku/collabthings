package org.libraryofthings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

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
		log.warning("ERROR in " + o + " " + sourceMethod + " throwable:" + e1);
		StringWriter w = new StringWriter();
		e1.printStackTrace(new PrintWriter(w));
		log.severe("ERROR in " + o + " " + w.getBuffer());
		//
		log.throwing(o.getClass().getName(), sourceMethod, e1);
	}

	public static LLog getLogger(Object o) {
		return new LLog(o);
	}

	public void fine(String string) {
		log.info(getLine(string));
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
		log.warning(getLine(string));
	}
}
