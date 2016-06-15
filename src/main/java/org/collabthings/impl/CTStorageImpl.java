package org.collabthings.impl;

import java.util.List;

import org.collabthings.CTStorage;
import org.collabthings.util.LLog;

import waazdoh.common.client.ServiceClient;
import waazdoh.common.vo.UserVO;

public class CTStorageImpl implements CTStorage {

	private ServiceClient service;
	private LLog log = LLog.getLogger(this);

	public CTStorageImpl(ServiceClient service) {
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
		String npath = fullpath;
		int indexOf = npath.indexOf('/');
		if (indexOf == 0) {
			npath = npath.substring(1);
			indexOf = npath.indexOf('/');
		}

		if (indexOf >= 0) {
			String username = npath.substring(0, indexOf);
			String path = npath.substring(indexOf + 1);
			String value = service.getStorageArea().read(username, path);
			log.info("readStorage username:" + username + " path:" + path + " got value:\"" + value + "\"");

			return value;
		} else {
			log.info("readStorage called without a path \"" + fullpath + "\"");
			return null;
		}
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
