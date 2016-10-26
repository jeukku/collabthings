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

import java.util.List;

import org.collabthings.CTStorage;
import org.collabthings.util.LLog;

import waazdoh.common.client.WServiceClient;
import waazdoh.common.vo.StorageAreaVO;
import waazdoh.common.vo.UserVO;

public class CTStorageImpl implements CTStorage {

	private WServiceClient service;
	private LLog log = LLog.getLogger(this);

	public CTStorageImpl(WServiceClient service) {
		this.service = service;
	}

	@Override
	public void writeToStorage(String path, String name, String data) {
		service.getStorageArea().write(new StorageAreaVO(path + "/" + name, data));
	}

	@Override
	public List<String> listStorage(String string) {
		return service.getStorageArea().list(new StorageAreaVO(string));
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
			String value = service.getStorageArea().read(new StorageAreaVO(username, path, null)).getData();
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
			return service.getStorageArea().read(new StorageAreaVO(u.getUsername(), item, null)).getData();
		} else {
			LLog.getLogger(this).info("User null -> returning null");
			return null;
		}
	}
}
