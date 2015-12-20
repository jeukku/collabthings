package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.model.LOTScript;

public final class TestPartBuilder extends LOTTestCase {

	public void testSaveAndLoad() throws IOException {
		LOTClient client = getNewClient();
		assertNotNull(client);
		//
		LOTPartBuilder pba = client.getObjectFactory().getPartBuilder();
		assertNotNull(pba);

		String name = "PartBuilderTest";
		pba.setName(name);

		String stext = loadATestScript("partbuilder/test.js");
		LOTScript s = client.getObjectFactory().getScript();
		s.setScript(stext);
		pba.setScript(s);

		pba.publish();

		//
		clientb = getNewClient();

		LOTPartBuilder pbb = clientb.getObjectFactory().getPartBuilder(
				pba.getID().getStringID());
		assertEquals(pba.getObject().toText(), pbb.getObject().toText());

		LOTPart p = clientb.getObjectFactory().getPart();
		boolean success = pbb.run(p);
		assertNull(pbb.getError());
		assertTrue(success);
		assertEquals(name, pbb.getName());
		assertEquals(1, p.getSubParts().size());
	}

}
