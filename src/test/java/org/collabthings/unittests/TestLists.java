package org.collabthings.unittests;

import java.io.IOException;
import java.util.List;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.xml.sax.SAXException;

public final class TestLists extends LOTTestCase {

	public void testWrite() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);
		//

		e.getStorage().writeToStorage("testpath", "testname", "testvalue");
		String username = e.getService().getUser().getUsername();
		String path = "testpath";
		List<String> list = e.getStorage().listStorage(path);
		assertEquals("testname", list.iterator().next());
		assertEquals(
				"testvalue",
				e.getStorage().readStorage(e.getService().getUser(),
						"testpath/testname"));
	}
}
