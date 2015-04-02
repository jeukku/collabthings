package org.libraryofthings.impl;

import java.util.List;

import org.libraryofthings.LOTStorage;

import waazdoh.common.client.ServiceClient;

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
	public String readStorage(String path, String name) {
		return service.getStorageArea().read(path + "/" + name);
	}
}
