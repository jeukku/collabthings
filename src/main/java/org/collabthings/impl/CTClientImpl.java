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
import org.collabthings.CTStorage;
import org.collabthings.factory.CTObjectFactory;
import org.collabthings.factory.impl.CTObjectFactoryImpl;
import org.collabthings.model.CTObject;
import org.collabthings.util.LLog;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.common.BeanStorage;
import waazdoh.common.WPreferences;
import waazdoh.common.client.WServiceClient;
import waazdoh.common.vo.StorageAreaVO;

public final class CTClientImpl implements CTClient {
	private final static String PREFIX = "CT";
	//
	private final WClient client;
	private final CTStorage storage;
	private final CTObjectFactory factory;
	private LLog log = LLog.getLogger(this);
	private CTBookmarks bookmarks;
	private Set<CTErrorListener> errorlisteners = new HashSet<>();

	public CTClientImpl(WPreferences p, BinarySource binarysource, BeanStorage beanstorage, WServiceClient service) {
		client = new WClient(p, binarysource, beanstorage, service);
		client.addObjectFilter((o) -> CTClient.checkVersion(o.getValue("version")));

		this.factory = new CTObjectFactoryImpl(this);
		this.storage = new CTStorageImpl(service);

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
	public CTStorage getStorage() {
		return storage;
	}

	@Override
	public String getVersion() {
		return CTClientImpl.VERSION;
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
		String path = "published/" + string;
		path = path.replace("//", "/");

		String id = "" + o.getID();

		log.info("writing " + path + " value:" + id);

		client.getService().getStorageArea().write(new StorageAreaVO(path, id));
	}

	@Override
	public String getPublished(String username, String string) {
		String path = "" + string;
		if (path.indexOf("published/") < 0) {
			path = "published/" + string;
		}

		path = path.replace("//", "/");
		log.info("reading " + username + " path:" + path);
		return client.getService().getStorageArea().read(new StorageAreaVO(username, path, null)).getData();
	}

	@Override
	public String getPublished(String value) {
		String path = "" + value;
		if (path.indexOf('/') == 0) {
			path = path.substring(1);
		}

		String username = path.substring(0, path.indexOf('/'));
		path = path.substring(path.indexOf('/'));

		return getPublished(username, path);
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
