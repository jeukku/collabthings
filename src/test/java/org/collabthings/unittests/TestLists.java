package org.collabthings.unittests;

import java.io.IOException;
import java.util.Map;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.xml.sax.SAXException;

public final class TestLists extends CTTestCase {

	public void testWrite() throws IOException, SAXException {
		CTClient e = getNewClient();
		assertNotNull(e);
		//

		String testpath = "/testpath/testname";
		e.getStorage().write(testpath, "testvalue");

		String path = "/testpath";
		Map<String, String> list = e.getStorage().getList(path);
		assertEquals(testpath, list.keySet().iterator().next());
		assertEquals("testvalue", list.values().iterator().next());
//		assertEquals("testvalue", e.getStorage().read(e.getService().getUser().getUserid(), testpath));
		assertEquals("testvalue", e.getStorage().read(testpath));
	}
}
