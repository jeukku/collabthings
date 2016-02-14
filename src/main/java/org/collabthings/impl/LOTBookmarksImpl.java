package org.collabthings.impl;

import java.util.Date;
import java.util.List;

import org.collabthings.LOTBookmarks;
import org.collabthings.LOTStorage;

public class LOTBookmarksImpl implements LOTBookmarks {

	private LOTStorage storage;
	private String username;

	public LOTBookmarksImpl(LOTClientImpl nclient) {
		this.storage = nclient.getStorage();
		this.username = nclient.getClient().getService().getUser()
				.getUsername();
	}

	@Override
	public List<String> list() {
		return getStorage().listStorage("bookmarks");
	}

	private LOTStorage getStorage() {
		return storage;
	}

	@Override
	public void add(String string) {
		getStorage().writeToStorage("bookmarks/" + string, "date",
				"" + new Date());
	}

	@Override
	public void add(String name, String value) {
		getStorage().writeToStorage("bookmarks/" + name, "bm", value);
	}

	@Override
	public String get(String string) {
		return getStorage().readStorage(
				this.username + "/bookmarks/" + string + "/bm");
	}
}
