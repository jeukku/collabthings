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

import org.collabthings.CTClient;
import org.collabthings.application.CTApplicationRunner;
import org.collabthings.core.ServiceObject;
import org.collabthings.core.ServiceObjectData;
import org.collabthings.datamodel.WObject;
import org.collabthings.datamodel.WObjectID;
import org.collabthings.datamodel.WStringID;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.util.LLog;

public class CTPartBuilderImpl implements CTPartBuilder, ServiceObjectData {
	public static final String BEANNAME = "partbuilder";

	private static final String VALUE_APPLICATION = "application";
	private static final String VALUE_NAME = "name";
	private static final String VALUE_ENV = "env";

	private CTClient client;
	private CTApplication application;
	private String error;

	private ServiceObject o;

	private String name;

	private CTEnvironment e;

	public CTPartBuilderImpl(CTClient client) {
		this.client = client;
		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
		e = new CTEnvironmentImpl(client);
		application = new CTApplicationImpl(client);
	}

	@Override
	public boolean run(CTPart p) {
		try {
			CTApplicationRunner runner = new CTApplicationRunner(application);
			CTRunEnvironment rune = new CTRunEnvironmentImpl(this.client, e);
			rune.addObject("part", p);
			runner.run(rune, null);
			return true;
		} catch (RuntimeException e) {
			LLog.getLogger(this).error("running partbuilder " + getName() + " got " + e);
			LLog.getLogger(this).error(this, "run", e);
			return false;
		}
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public void setName(String string) {
		this.name = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setApplication(CTApplication a) {
		this.application = a;
	}

	@Override
	public CTApplication getApplication() {
		return this.application;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void publish() {
		save();

		application.publish();
		e.publish();
		o.publish();
		client.publish(getName(), this);
	}

	@Override
	public void save() {
		application.save();
		e.save();
		o.save();
	}

	@Override
	public WObjectID getID() {
		return o.getID();
	}

	@Override
	public WObject getObject() {
		WObject content = o.getBean();

		if (application != null) {
			content.addValue(VALUE_APPLICATION, this.application.getID());
		}

		content.addValue(VALUE_NAME, getName());
		content.addValue(VALUE_ENV, e.getID().toString());

		return content;
	}

	@Override
	public boolean parse(WObject o) {
		String valueapplication = o.getValue(VALUE_APPLICATION);
		if (valueapplication != null) {
			WStringID applicationid = new WStringID(valueapplication);
			setApplication(client.getObjectFactory().getApplication(applicationid));
		} else if (o.getValue("script") != null) {
			LLog.getLogger(this).error("not loading script with id " + o.getValue("script"));
		}
		setName(o.getValue(VALUE_NAME));
		e = new CTEnvironmentImpl(client, new WStringID(o.getValue(VALUE_ENV)));
		return true;
	}

	@Override
	public boolean load(WStringID builderid) {
		o = new ServiceObject(BEANNAME, client.getClient(), this, client.getVersion(), client.getPrefix());
		return o.load(builderid);
	}

	@Override
	public int hashCode() {
		return getObject().toYaml().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj.getClass() == this.getClass()) {
			CTPartBuilderImpl builder = (CTPartBuilderImpl) obj;
			return getObject().toYaml().equals(builder.getObject().toYaml());
		} else {
			return false;
		}
	}

}
