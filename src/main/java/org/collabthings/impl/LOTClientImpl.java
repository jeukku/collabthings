package org.collabthings.impl;

import org.collabthings.LOTBookmarks;
import org.collabthings.LOTClient;
import org.collabthings.LOTStorage;
import org.collabthings.factory.LOTObjectFactory;
import org.collabthings.factory.impl.LOTObjectFactoryImpl;
import org.collabthings.model.LOTObject;
import org.collabthings.util.LLog;

import waazdoh.client.BinarySource;
import waazdoh.client.WClient;
import waazdoh.common.BeanStorage;
import waazdoh.common.WPreferences;
import waazdoh.common.client.ServiceClient;

public final class LOTClientImpl implements LOTClient {
	private final static String PREFIX = "LOT";
	//
	private final WClient client;
	private final LOTStorage storage;
	private final LOTObjectFactory factory;
	private LLog log = LLog.getLogger(this);
	private LOTBookmarks bookmarks;

	public LOTClientImpl(WPreferences p, BinarySource binarysource,
			BeanStorage beanstorage, ServiceClient service) {
		client = new WClient(p, binarysource, beanstorage, service);
		client.addObjectFilter((o) -> {
			return LOTClient.checkVersion(o.getValue("version"));
		});

		this.factory = new LOTObjectFactoryImpl(this);
		this.storage = new LOTStorageImpl(service);
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
	public LOTObjectFactory getObjectFactory() {
		return factory;
	}

	@Override
	public LOTStorage getStorage() {
		return storage;
	}

	@Override
	public String getVersion() {
		return LOTClientImpl.VERSION;
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
		return client
				.readStorageArea("/public/" + PREFIX + "/settings/" + name);
	}

	@Override
	public String toString() {
		return "" + this.client;
	}

	@Override
	public ServiceClient getService() {
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
	public void publish(String name, LOTObject o) {
		log.info("publish " + name + " " + o);

		createPublishedStorage("/" + o.getObject().getType() + "/" + name + "/"
				+ LOTClient.getDateTime(), o);
		createPublishedStorage("/" + o.getObject().getType() + "/" + name
				+ "/latest", o);
		createPublishedStorage("/" + o.getObject().getType() + "/" + name, o);
		createPublishedStorage("/" + o.getObject().getType() + "/latest", o);

		log.info("published " + name + " " + o);
	}

	private void createPublishedStorage(String string, LOTObject o) {
		String path = "published/" + string;
		path = path.replace("//", "/");

		String id = "" + o.getID();

		log.info("writing " + path + " value:" + id);

		client.getService().getStorageArea().write(path, id);
	}

	@Override
	public String getPublished(String username, String string) {
		String path = "" + string;
		if (path.indexOf("published/") < 0) {
			path = "published/" + string;
		}

		path = path.replace("//", "/");
		log.info("reading " + username + " path:" + path);
		return client.getService().getStorageArea().read(username, path);
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
	public LOTBookmarks getBookmarks() {
		if (bookmarks == null) {
			this.bookmarks = new LOTBookmarksImpl(this);
		}

		return bookmarks;
	}
}
