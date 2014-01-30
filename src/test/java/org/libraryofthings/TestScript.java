package org.libraryofthings;

import java.io.IOException;

import org.libraryofthings.model.LOTScript;
import org.xml.sax.SAXException;

public final class TestScript extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOTScript s = new LOTScript(env);
		s.setScript("failing script");
		assertTrue(s.getServiceObject().save());
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOTScript bs = new LOTScript(benv, s.getServiceObject().getID()
				.getStringID());
		assertEquals(bs.getScript(), s.getScript());
	}

}
