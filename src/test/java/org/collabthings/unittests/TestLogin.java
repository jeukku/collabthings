package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.xml.sax.SAXException;

public final class TestLogin extends CTTestCase {

	public void testClient() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);

		assertNotNull(c.getService());
		assertNotNull(c.getClient().getUser(c.getClient().getUserID()));
	}


}
