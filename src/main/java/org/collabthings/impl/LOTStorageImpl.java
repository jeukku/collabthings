package org.collabthings.impl;

import java.util.List;

import org.collabthings.LOTStorage;

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
	public String readStorage(UserVO u, String item) {
		return service.getStorageArea().read(u.getUsername(), item);
	}
}
