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
import java.util.List;

import org.collabthings.CTBookmarks;
import org.collabthings.CTStorage;

public class CTBookmarksImpl implements CTBookmarks {

	private static final String BOOKMARKS = "bookmarks/";
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
		return getStorage().listStorage(BOOKMARKS);
	}

	@Override
	public List<String> list(String string) {
		return getStorage().listStorage(BOOKMARKS + string);
	}

	private CTStorage getStorage() {
		return storage;
	}

	@Override
	public void addFolder(String string) {
		getStorage().writeToStorage(BOOKMARKS + string, DATA_VARIABLE, "" + new Date());
	}

	@Override
	public void add(String name, String value) {
		getStorage().writeToStorage(BOOKMARKS + name, BM_VARIABLE, value);
	}

	@Override
	public String get(String string) {
		return getStorage().readStorage(this.username + "/bookmarks/" + string + "/" + BM_VARIABLE);
	}
}
