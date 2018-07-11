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

package org.collabthings.model.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.collabthings.CTClient;
import org.collabthings.application.handlers.CTLogHandler;
import org.collabthings.model.CTApplication;
import org.collabthings.util.LLog;

import waazdoh.client.ServiceObject;
import waazdoh.client.ServiceObjectData;
import waazdoh.datamodel.WObject;
import waazdoh.datamodel.WObjectID;
import waazdoh.datamodel.WStringID;

/**
 * 
 * @author Juuso Vilmunen
 * 
 */
public final class CTApplicationImpl implements ServiceObjectData, CTApplication {
	private static final String PARAM_LINES = "lines";
	private static final String BEANNAME = "app";
	//
	private ServiceObject o;

	//
	private LLog log = LLog.getLogger(this);
	private final CTClient client;
	//
	private static int namecounter;
	private String name;
	private String info;
	private List<ApplicationLine> lines;

	/**
	 * Creates a new application with random ID.
	 * 
	 * @param env
	 */
	public CTApplicationImpl(final CTClient env) {
		this.client = env;
		o = new ServiceObject(BEANNAME, env.getClient(), this, env.getVersion(), env.getPrefix());
		setName("application" + CTApplicationImpl.namecounter);
		CTApplicationImpl.namecounter++;
		lines = new LinkedList<>();
		ApplicationLine logline = new ApplicationLine();
		logline.put(ApplicationLine.ACTION, "log");
		logline.put(CTLogHandler.MSG, "Running " + getName());
		lines.add(logline);
	}

	@Override
	public WObject getObject() {
		WObject b = o.getBean();
		b.addValue("name", name);
		for (ApplicationLine line : lines) {
			b.addToList(PARAM_LINES, line.getWObject());
		}
		return b;
	}

	@Override
	public boolean isOK() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<ApplicationLine> getLines() {
		return lines;
	}

	@Override
	public void addApplicationLine(ApplicationLine line) {
		lines.add(line);
	}

	@Override
	public boolean parse(final WObject bean) {
		List<Map> lineslist = bean.getList(PARAM_LINES);
		lines.clear();
		parseLines(lineslist);

		this.name = bean.getValue("name");
		return name != null && lines != null;
	}

	@Override
	public boolean load(WStringID id) {
		return o.load(id);
	}

	@Override
	public void setApplication(final String napplication) {
		WObject o = new WObject();
		o.parse(napplication);
		parseLines(o.getList(PARAM_LINES));
	}

	private void parseLines(List<Map> list) {
		for (Map wo : list) {
			this.lines.add(new ApplicationLine(wo));
		}
	}

	private ServiceObject getServiceObject() {
		return o;
	}

	@Override
	public WObjectID getID() {
		return getServiceObject().getID();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void publish() {
		getServiceObject().publish();
	}

	@Override
	public void save() {
		getServiceObject().save();
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return Return value of info -function in the application.
	 * @throws NoSuchMethodException
	 * @throws ApplicationException
	 */
	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return "CTApplication[" + this.name + "][" + info + "]";
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public static class ApplicationLine {
		public static final String ACTION = "a";

		private Map<String, String> values = new HashMap<>();

		public ApplicationLine() {
			//
		}

		public ApplicationLine(Map wo) {
			for (Object key : wo.keySet()) {
				values.put("" + key, "" + wo.get(key));
			}
		}

		public void put(String key, String value) {
			values.put(key, value);
		}

		public WObject getWObject() {
			WObject wo = new WObject();
			for (String key : values.keySet()) {
				wo.addValue(key, values.get(key));
			}
			return wo;
		}

		public String get(String key) {
			return values.get(key);
		}

	}
}
