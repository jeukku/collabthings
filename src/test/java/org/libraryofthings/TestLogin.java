package org.libraryofthings;

import java.io.IOException;

import org.xml.sax.SAXException;

import waazdoh.client.URLCaller;
import waazdoh.client.WClientAppLogin;

public final class TestLogin extends LOTTestCase {

	public void testClient() throws IOException, SAXException {
		LOTEnvironment c = getNewEnv();
		assertNotNull(c);
	}

	public void testAppLogin() throws IOException, SAXException {
		LOTEnvironment e = getNewEnv();
		assertNotNull(e);
		WClientAppLogin applogin = e.getClient().requestAppLogin();
		assertNotNull(applogin);
		assertNotNull(applogin.getURL());
		
		e.getClient().getService().acceptAppLogin(applogin.getId());
		applogin = e.getClient().checkAppLogin(applogin.getId());
	}
}
