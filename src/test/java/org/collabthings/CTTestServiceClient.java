package org.collabthings;

import java.util.UUID;

import org.collabthings.common.service.ObjectsService;
import org.collabthings.common.service.StorageAreaService;
import org.collabthings.common.service.UsersService;
import org.collabthings.core.BinarySource;
import org.collabthings.core.WServiceClient;
import org.collabthings.core.utils.WPreferences;
import org.collabthings.datamodel.UserVO;

public class CTTestServiceClient implements WServiceClient {

	private WPreferences p;
	private StaticObjectsService objects;
	private StorageAreaService storagearea;
	private UserVO user;
	private StaticUsersService users;
	private final String username;
	private BinarySource binarysource;

	private CTTestServiceClient(String username, WPreferences p) {
		this.p = p;
		this.username = username;
	}

	@Override
	public StorageAreaService getStorageArea() {
		if (storagearea == null) {
			storagearea = new StaticStorageAreaService();
		}
		return storagearea;
	}

	@Override
	public UsersService getUsers() {
		if (users == null) {
			user = new UserVO();
			user.setUserid(p.get("userid", UUID.randomUUID().toString()));
			user.setUsername(username);

			users = new StaticUsersService(user);
		}
		return users;
	}

	@Override
	public ObjectsService getObjects() {
		if (objects == null) {
			objects = new StaticObjectsService(p);
		}
		return objects;
	}

	@Override
	public UserVO getUser() {
		if (user == null) {
			getUsers();
		}
		return user;
	}

	@Override
	public UserVO getUser(String username) {
		return getUsers().getWithName(username);
	}

	@Override
	public BinarySource getBinarySource() {
		if (binarysource == null) {
			binarysource = new StaticBinarySource(this);
		}
		return binarysource;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
