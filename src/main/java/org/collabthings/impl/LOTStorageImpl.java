package org.collabthings.impl;

import java.util.List;

import org.collabthings.LOTStorage;
import org.collabthings.util.LLog;

import waazdoh.common.client.ServiceClient;
import waazdoh.common.vo.UserVO;

public class LOTStorageImpl implements LOTStorage {

	private ServiceClient service;

	public LOTStorageImpl(ServiceClient service) {
		this.service = service;
	}

	@Override
	public void writeToStorage(String path, String name, String data) {
		service.getStorageArea().write(path + "/" + name, data);
	}

	@Override
	public List<String> listStorage(String string) {
		return service.getStorageArea().list(string);
	}

	@Override
	public List<String> getUserPublished(String userid, int start, int count) {
		return service.getStorageArea().listNewItems(userid, start, count);
	}

	@Override
	public String readStorage(final String fullpath) {
		String username = fullpath.substring(0, fullpath.indexOf('/'));
		int indexOf = fullpath.indexOf('/');
		String path = fullpath.substring(indexOf + 1);
		String value = service.getStorageArea().read(username, path);
		LLog.getLogger(this).info(
				"readStorage username:" + username + " path:" + path
						+ " got value:\"" + value + "\"");

		return value;
	}

	@Override
	public String readStorage(UserVO u, String item) {
		if (u != null) {
			return service.getStorageArea().read(u.getUsername(), item);
		} else {
			LLog.getLogger(this).info("User null -> returning null");
			return null;
		}
	}
}
