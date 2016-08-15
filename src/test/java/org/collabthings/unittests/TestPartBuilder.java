package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTPart;
import org.collabthings.model.CTPartBuilder;
import org.collabthings.model.CTScript;

public final class TestPartBuilder extends CTTestCase {

	public void testSaveAndLoad() throws IOException {
		CTClient client = getNewClient();
		assertNotNull(client);
		//
		CTPartBuilder pba = client.getObjectFactory().getPartBuilder();
		assertNotNull(pba);

		String name = "PartBuilderTest";
		pba.setName(name);

		String stext = loadATestScript("partbuilder/test.js");
		CTScript s = client.getObjectFactory().getScript();
		s.setScript(stext);
		pba.setScript(s);

		pba.publish();

		//
		clientb = getNewClient();

		CTPartBuilder pbb = clientb.getObjectFactory().getPartBuilder(pba.getID().getStringID());
		assertEquals(pba.getObject().toYaml(), pbb.getObject().toYaml());

		CTPart p = clientb.getObjectFactory().getPart();
		boolean success = pbb.run(p);
		assertNull(pbb.getError());
		assertTrue(success);
		assertEquals(name, pbb.getName());
		assertEquals(1, p.getSubParts().size());
	}

}
