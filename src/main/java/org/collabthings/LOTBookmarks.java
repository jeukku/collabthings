package org.collabthings;

import java.util.List;

public interface LOTBookmarks {

	void addFolder(String string);

	List<String> list();

	void add(String string, String string2);

	String get(String string);

	List<String> list(String string);

}
