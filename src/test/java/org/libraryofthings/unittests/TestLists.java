package org.libraryofthings.unittests;

import java.io.IOException;
import java.util.Set;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

public final class TestLists extends LOTTestCase {

	public void testWrite() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);
		//

		e.getStorage().writeToStorage("testpath", "testname", "testvalue");
		Set<String> list = e.getStorage().listStorage("testpath");
		assertEquals("testname", list.iterator().next());
		assertEquals("testvalue",
				e.getStorage().readStorage("testpath", "testname"));
	}
}
