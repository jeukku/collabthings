package org.libraryofthings;

import java.io.IOException;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;

public final class TestLogin extends LOTTestCase {

	public void testClient() throws IOException, SAXException {
		LOTEnvironment c = getNewEnv();
		assertNotNull(c);
	}

	public void testLocalLogin() throws MalformedURLException, SAXException {
		LOTEnvironment c = getNewEnv("test@localhost", true);
		assertNotNull(c);
	}
}
