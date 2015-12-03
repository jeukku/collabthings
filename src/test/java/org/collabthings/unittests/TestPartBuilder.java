package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.model.LOTEnvironment;
import org.collabthings.model.LOTPart;
import org.collabthings.model.LOTPartBuilder;
import org.collabthings.model.LOTScript;
import org.collabthings.model.impl.LOTEnvironmentImpl;

public final class TestPartBuilder extends LOTTestCase {

	public void testSaveAndLoad() throws IOException {
		LOTClient client = getNewClient();
		assertNotNull(client);
		//
		LOTPartBuilder pb = client.getObjectFactory().getPartBuilder();
		assertNotNull(pb);

		String stext = loadATestScript("partbuilder/test.js");
		LOTScript s = client.getObjectFactory().getScript();
		s.setScript(stext);
		pb.setScript(s);
		//
		LOTEnvironment env = new LOTEnvironmentImpl(client);
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(client, env);

		LOTPart p = client.getObjectFactory().getPart();
		assertTrue(pb.run(p));

	}

}
