package org.libraryofthings;

import java.io.IOException;

import org.xml.sax.SAXException;

public final class TestLogin extends LOTTestCase {

	public void testClient() throws IOException, SAXException {
		LOTEnvironment c = getNewEnv();
		assertNotNull(c);
	}
}
