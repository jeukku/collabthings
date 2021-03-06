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

package org.collabthings.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.collabthings.CTBookmarks;
import org.collabthings.CTClient;
import org.collabthings.CTErrorListener;
import org.collabthings.common.service.StorageAreaService;
import org.collabthings.core.BeanStorage;
import org.collabthings.core.BinarySource;
import org.collabthings.core.WClient;
import org.collabthings.core.WServiceClient;
import org.collabthings.core.utils.WPreferences;
import org.collabthings.factory.CTObjectFactory;
import org.collabthings.factory.impl.CTObjectFactoryImpl;
import org.collabthings.model.CTObject;
import org.collabthings.model.impl.CTConstants;
import org.collabthings.util.LLog;

public final class CTClientImpl implements CTClient {
	private static final String PUBLISHED = "published/";
	private static final String PREFIX = "CT";
	//
	private final WClient client;
	private final CTObjectFactory factory;
	private LLog log = LLog.getLogger(this);
	private CTBookmarks bookmarks;
	private Set<CTErrorListener> errorlisteners = new HashSet<>();

	public CTClientImpl(WPreferences p, BeanStorage beanstorage, WServiceClient service) {
		client = new WClient(p, service);
		client.addObjectFilter(o -> CTClient.checkVersion(o.getValue("version")));

		this.factory = new CTObjectFactoryImpl(this);

		p.set("date", "" + new Date());
	}

	@Override
	public WClient getClient() {
		return client;
	}

	@Override
	public BinarySource getBinarySource() {
		return client.getBinarySource();
	}

	@Override
	public CTObjectFactory getObjectFactory() {
		return factory;
	}

	@Override
	public StorageAreaService getStorage() {
		return client.getService().getStorageArea();
	}

	@Override
	public String getVersion() {
		return CTConstants.VERSION;
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public WPreferences getPreferences() {
		return client.getPreferences();
	}

	@Override
	public void stop() {
		getClient().stop();
	}

	@Override
	public String getGlobalSetting(String name) {
		return client.readStorageArea("/public/" + PREFIX + "/settings/" + name);
	}

	@Override
	public String toString() {
		return "" + this.client;
	}

	@Override
	public WServiceClient getService() {
		return getClient().getService();
	}

	@Override
	public boolean isRunning() {
		if (getClient().isRunning()) {
			return true;
		}

		return false;
	}

	@Override
	public void publish(String name, CTObject o) {
		log.info("publish " + name + " " + o);

		createPublishedStorage("/" + o.getObject().getType() + "/" + name + "/" + CTClient.getDateTime(), o);
		createPublishedStorage("/" + o.getObject().getType() + "/" + name + "/latest", o);
		createPublishedStorage("/" + o.getObject().getType() + "/latest", o);

		log.info("published " + name + " " + o);
	}

	private void createPublishedStorage(String string, CTObject o) {
		String path = PUBLISHED + string;
		path = path.replace("//", "/");

		String id = "" + o.getID();

		log.info("writing " + path + " value:" + id);

		client.getService().getStorageArea().write(path, id);
	}

	@Override
	public CTBookmarks getBookmarks() {
		if (bookmarks == null) {
			this.bookmarks = new CTBookmarksImpl(this);
		}

		return bookmarks;
	}

	@Override
	public void addErrorListener(CTErrorListener listener) {
		errorlisteners.add(listener);
	}

	@Override
	public void errorEvent(String error, Exception e) {
		errorlisteners.stream().forEach(l -> l.error(error, e));
	}
}
