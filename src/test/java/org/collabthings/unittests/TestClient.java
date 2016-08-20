package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTErrorListener;
import org.collabthings.CTTestCase;
import org.xml.sax.SAXException;

public final class TestClient extends CTTestCase {

	public void testErrorEvents() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);

		assertNotNull(c.getService());
		assertNotNull(c.getClient().getUser(c.getClient().getUserID()));

		StringBuffer sb = new StringBuffer("Shouldn't be empty");

		c.addErrorListener(new CTErrorListener() {

			@Override
			public void error(String error, Exception e) {
				sb.setLength(0);
				sb.append(error.toString());
			}
		});

		String string = "clienttest-ERRORMESSAGE";
		c.errorEvent(string, new IllegalArgumentException("this is ok"));

		assertEquals(string, sb.toString());
	}

}
