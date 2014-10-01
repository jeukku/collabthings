package org.libraryofthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.model.LOTScript;
import org.libraryofthings.model.LOTTool;
import org.libraryofthings.model.impl.LOTToolImpl;
import org.xml.sax.SAXException;

public final class TestTool extends LOTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		LOTClient env = getNewClient();
		assertNotNull(env);
		//
		env.getObjectFactory().getTool();
		LOTTool t = env.getObjectFactory().getTool();
		assertNotNull(t);
		t.save();
		//
		assertNotNull(env.getObjectFactory().getTool(t.getID().getStringID()));
		assertEquals(t, env.getObjectFactory().getTool(t.getID().getStringID()));
	}

	public void testSaveAndLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTClient env = getNewClient(true);
		assertNotNull(env);
		//
		LOTTool t = env.getObjectFactory().getTool();
		t.setName("testing changing name");
		t.save();
		//
		LOTScript lotScript = t.addScript("test");
		lotScript
				.setScript("function info() { return \"testing tool script\"; }");
		//
		t.newPart();
		String testbinarydatastring = "TESTIBINARYDATA";
		t.getPart().getModel().getBinary()
				.add(new String(testbinarydatastring).getBytes());
		//
		t.save();
		t.publish();
		//
		LOTClient benv = getNewClient(true);
		assertNotNull(benv);
		LOTTool btool = benv.getObjectFactory()
				.getTool(t.getID().getStringID());
		assertEquals(btool.getName(), t.getName());
		waitObject(btool);
		//
		LOTScript bscript = btool.getScript("test");
		assertNotNull(bscript);
		assertEquals(lotScript.getScript(), bscript.getScript());
		//
		String sdata = new String(btool.getPart().getModel().getBinary()
				.asByteBuffer());
		assertEquals(testbinarydatastring, sdata);
	}

	public void testNullPart() throws IOException, SAXException {
		LOTToolImpl t = new LOTToolImpl(getNewClient());
		t.save();
		t.publish();
		//
		LOTTool b = getNewClient().getObjectFactory().getTool(
				t.getID().getStringID());
		assertTrue(b.isReady());
		assertTrue(b.toString().indexOf("LOTTool") >= 0);
	}

	public void testCallUnknownScript() throws IOException, SAXException {
		LOTClient e = getNewClient();
		LOTTool tool = e.getObjectFactory().getTool();
		assertNull(tool.getScript("FAIL"));
	}

	public void testAddGetScript() {
		LOTClient c = getNewClient();
		LOTTool tool = c.getObjectFactory().getTool();
		tool.addScript("testscript");
		assertNotNull(tool.getScript("testscript"));
	}
}
