package org.collabthings;

import java.util.LinkedList;
import java.util.List;

import collabthings.common.service.UsersService;
import collabthings.datamodel.ProfileVO;
import collabthings.datamodel.ReturnVO;
import collabthings.datamodel.UserVO;

public class StaticUsersService implements UsersService {
	private static List<UserVO> users = new LinkedList<>();

	public StaticUsersService(UserVO user) {
		StaticUsersService.users.add(user);
	}

	@Override
	public ProfileVO getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserVO getUser(String userid) {
		for (UserVO userVO : users) {
			if (userVO.getUserid().equals(userid)) {
				return userVO;
			}
		}
		return null;
	}

	@Override
	public UserVO getWithName(String username) {
		for (UserVO userVO : users) {
			if (userVO.getUsername().equals(username)) {
				return userVO;
			}
		}
		return null;
	}

	@Override
	public List<UserVO> search(String string, int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserVO> newUsers(int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnVO saveProfile(ProfileVO profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void follow(String userid) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getFollowing() {
		// TODO Auto-generated method stub
		return null;
	}
}
