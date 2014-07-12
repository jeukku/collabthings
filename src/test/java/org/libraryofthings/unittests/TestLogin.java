package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

import waazdoh.client.WClientAppLogin;

public final class TestLogin extends LOTTestCase {

	public void testClient() throws IOException, SAXException {
		LOTClient c = getNewEnv();
		assertNotNull(c);
	}

	public void testAppLogin() throws IOException, SAXException {
		LOTClient e = getNewEnv();
		assertNotNull(e);
		WClientAppLogin applogin = e.getClient().requestAppLogin();
		assertNotNull(applogin);
		assertNotNull(applogin.getURL());
		
		e.getClient().getService().acceptAppLogin(applogin.getId());
		applogin = e.getClient().checkAppLogin(applogin.getId());
	}
}
