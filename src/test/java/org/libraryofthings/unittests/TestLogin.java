package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

import waazdoh.common.vo.AppLoginVO;

public final class TestLogin extends LOTTestCase {

	public void testClient() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);

		assertNotNull(c.getService());
		assertNotNull(c.getClient().getUser(c.getClient().getUserID()));
	}

	public void testAppLogin() throws IOException, SAXException {
		LOTClient e = getNewClient();
		assertNotNull(e);
		AppLoginVO applogin = e.getClient().requestAppLogin();
		assertNotNull(applogin);
		assertNotNull(applogin.getUrl());

		e.getClient().getService().getUsers().acceptApplication(applogin.getId());
		applogin = e.getClient().checkAppLogin(applogin.getId());
	}
}
