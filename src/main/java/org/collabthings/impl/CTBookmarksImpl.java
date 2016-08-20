package org.collabthings.impl;

import java.util.Date;
import java.util.List;

import org.collabthings.CTBookmarks;
import org.collabthings.CTStorage;

public class CTBookmarksImpl implements CTBookmarks {

	private static final String BM_VARIABLE = "bm";
	private static final String DATA_VARIABLE = "_date";
	private CTStorage storage;
	private String username;

	public CTBookmarksImpl(CTClientImpl nclient) {
		this.storage = nclient.getStorage();
		this.username = nclient.getClient().getService().getUser().getUsername();
	}

	@Override
	public List<String> list() {
		return getStorage().listStorage("bookmarks");
	}

	@Override
	public List<String> list(String string) {
		return getStorage().listStorage("bookmarks/" + string);
	}

	private CTStorage getStorage() {
		return storage;
	}

	@Override
	public void addFolder(String string) {
		getStorage().writeToStorage("bookmarks/" + string, DATA_VARIABLE, "" + new Date());
	}

	@Override
	public void add(String name, String value) {
		getStorage().writeToStorage("bookmarks/" + name, BM_VARIABLE, value);
	}

	@Override
	public String get(String string) {
		return getStorage().readStorage(this.username + "/bookmarks/" + string + "/" + BM_VARIABLE);
	}
}
