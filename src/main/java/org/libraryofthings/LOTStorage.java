package org.libraryofthings;

import java.util.List;

import waazdoh.common.vo.UserVO;

public interface LOTStorage {
	void writeToStorage(String path, String name, String data);

	List<String> listStorage(String string);

	List<String> getUserPublished(String userid, int start, int count);

	String readStorage(UserVO u, String item);
}
