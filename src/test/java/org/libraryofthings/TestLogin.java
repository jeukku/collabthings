package org.libraryofthings;

import java.io.IOException;

import org.xml.sax.SAXException;

public class TestLogin extends LOTTestCase {

	public void testClient() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
	}
}
