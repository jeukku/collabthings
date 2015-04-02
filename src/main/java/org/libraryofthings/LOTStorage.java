package org.libraryofthings;

import java.util.List;
import java.util.Set;

public interface LOTStorage {
	void writeToStorage(String path, String name, String data);

	List<String> listStorage(String string);

	String readStorage(String path, String name);
}
