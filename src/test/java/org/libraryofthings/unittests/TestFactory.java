package org.libraryofthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.xml.sax.SAXException;

public final class TestFactory extends LOTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		LOTClient env = getNewClient();
		assertNotNull(env);
		//
		env.getObjectFactory().getFactory();
		LOTFactory f = env.getObjectFactory().getFactory();
		assertNotNull(f);
		f.save();
		//
		assertNotNull(env.getObjectFactory()
				.getFactory(f.getID().getStringID()));
		assertEquals(f,
				env.getObjectFactory().getFactory(f.getID().getStringID()));
	}

	public void testSaveAndLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTClient env = getNewClient(true);
		assertNotNull(env);
		//
		LOTFactory f = env.getObjectFactory().getFactory();
		f.setName("testing changing name");
		f.save();
		//

		LOTScript lotScript = f.addScript("test");
		lotScript
				.setScript("function info() { return \"testing tool script\"; }");
		//
		f.save();
		f.publish();
		//
		LOTClient benv = getNewClient(true);
		assertNotNull(benv);
		LOTFactory bfact = benv.getObjectFactory().getFactory(
				f.getID().getStringID());
		assertEquals(bfact.getName(), f.getName());
		waitObject(bfact);
		//
		LOTScript bscript = bfact.getScript("test");
		assertNotNull(bscript);
		assertEquals(lotScript.getScript(), bscript.getScript());
	}

	public void testCallUnknownScript() throws IOException, SAXException {
		LOTClient e = getNewClient();
		LOTFactory factory = e.getObjectFactory().getFactory();
		assertNull(factory.getScript("FAIL"));
	}

	public void testAddGetScript() {
		LOTClient c = getNewClient();
		LOTTool tool = c.getObjectFactory().getTool();
		LOTScript s = tool.addScript("testscript");
		assertNotNull(tool.getScript("testscript"));
	}
}
