package org.collabthings.unittests;

import java.io.IOException;
import java.util.List;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.xml.sax.SAXException;

public final class TestBookmarks extends LOTTestCase {

	public void testGet() throws IOException, SAXException {
		disableNetwork();

		LOTClient e = getNewClient(false);
		e.getBookmarks().add("test");
		List<String> bookmarks = e.getBookmarks().list();
		assertNotNull(bookmarks);
		assertTrue(bookmarks.size() > 0);

		String testvalue = "testvalue";
		e.getBookmarks().add("test/testbm", testvalue);
		assertEquals(testvalue, e.getBookmarks().get("test/testbm"));
	}
}
