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
import java.util.Map;

import org.collabthings.CTBookmarks;
import org.collabthings.common.service.StorageAreaService;

public class CTBookmarksImpl implements CTBookmarks {

	private static final String BOOKMARKS = "/bookmarks/";
	private static final String BM_VARIABLE = "bm";
	private static final String DATA_VARIABLE = "_date";
	private StorageAreaService storage;

	public CTBookmarksImpl(CTClientImpl nclient) {
		this.storage = nclient.getStorage();
	}

	@Override
	public Map<String, String> list() {
		return getStorage().getList(BOOKMARKS);
	}

	@Override
	public Map<String, String> list(String string) {
		return getStorage().getList(BOOKMARKS + string);
	}

	private StorageAreaService getStorage() {
		return storage;
	}

	@Override
	public void addFolder(String string) {
		getStorage().write(BOOKMARKS + string + "/" + DATA_VARIABLE, "" + new Date());
	}

	@Override
	public void add(String name, String value) {
		getStorage().write(BOOKMARKS + name + "/" + BM_VARIABLE, value);
	}

	@Override
	public String get(String string) {
		return getStorage().read(BOOKMARKS + string + "/" + BM_VARIABLE);
	}
}
