package org.libraryofthings.impl;

import java.util.Set;

import org.libraryofthings.LOTStorage;

import waazdoh.client.service.WService;

public class LOTStorageImpl implements LOTStorage {

	private WService service;

	public LOTStorageImpl(WService service) {
		this.service = service;
	}

	@Override
	public void writeToStorage(String path, String name, String data) {
		service.writeStorageArea(path + "/" + name, data);
	}

	@Override
	public Set<String> listStorage(String string) {
		return service.listStorageArea(string);
	}

	@Override
	public String readStorage(String path, String name) {
		return service.readStorageArea(path + "/" + name);
	}
}
