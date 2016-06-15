package org.collabthings.unittests;

import java.io.IOException;
import java.util.List;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.xml.sax.SAXException;

public final class TestBookmarks extends CTTestCase {

	public void testGet() throws IOException, SAXException {
		disableNetwork();

		CTClient e = getNewClient(false);
		String listname = "test" + System.currentTimeMillis();

		e.getBookmarks().addFolder(listname);
		List<String> bookmarks = e.getBookmarks().list();
		assertNotNull(bookmarks);
		assertTrue(bookmarks.size() > 0);

		String testvalue = "testvalue";
		e.getBookmarks().add(listname + "/testbm", testvalue);
		assertEquals(testvalue, e.getBookmarks().get(listname + "/testbm"));

		List<String> testlist = e.getBookmarks().list(listname);
		assertNotNull(testlist);
		assertEquals(2, testlist.size()); // testbm and _date

	}
}
