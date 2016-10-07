package org.collabthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.CTToolException;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.environment.impl.CTToolState;
import org.collabthings.model.CTBinaryModel;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.CTScript;
import org.collabthings.model.CTTool;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTToolImpl;
import org.xml.sax.SAXException;

public final class TestTool extends CTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		CTClient env = getNewClient();
		assertNotNull(env);
		//
		env.getObjectFactory().getTool();
		CTTool t = env.getObjectFactory().getTool();
		assertNotNull(t);
		t.save();
		//
		assertNotNull(env.getObjectFactory().getTool(t.getID().getStringID()));
		assertEquals(t, env.getObjectFactory().getTool(t.getID().getStringID()));
	}

	public void testSaveAndLoad() throws IOException, SAXException, NoSuchMethodException, ScriptException {
		createTwoClients();
		//
		CTTool t = clienta.getObjectFactory().getTool();
		t.setName("testing changing name");
		t.save();
		//
		CTScript ctScript = t.addScript("test");
		ctScript.setScript("function info() { return \"testing tool script\"; }");
		//
		t.newPart();
		String testbinarydatastring = "TESTIBINARYDATA";
		CTBinaryModel model = t.getPart().newBinaryModel();
		model.setType("bin");
		model.getBinary().add(new String(testbinarydatastring).getBytes(CTClient.CHARSET));
		//
		t.save();
		t.publish();
		//
		assertNotNull(clientb);
		CTTool btool = clientb.getObjectFactory().getTool(t.getID().getStringID());
		assertEquals(btool.getName(), t.getName());
		waitObject(btool);
		//
		CTScript bscript = btool.getScript("test");
		assertNotNull(bscript);
		assertEquals(ctScript.getScript(), bscript.getScript());
	}

	public void testNullPart() throws IOException, SAXException {
		CTToolImpl t = new CTToolImpl(getNewClient());
		t.save();
		t.publish();
		//
		CTTool b = getNewClient().getObjectFactory().getTool(t.getID().getStringID());
		assertTrue(b.isReady());
		assertTrue(b.toString().indexOf("CTTool") >= 0);
	}

	public void testGetUnknownScript() throws IOException, SAXException {
		CTClient e = getNewClient();
		CTTool tool = e.getObjectFactory().getTool();
		assertNull(tool.getScript("FAIL"));
	}

	public void testCallUnknownScript() {
		CTClient c = getNewClient();
		CTTool tool = c.getObjectFactory().getTool();
		CTEnvironment env = new CTEnvironmentImpl(c);
		CTRunEnvironment runenv = new CTRunEnvironmentImpl(c, env);
		CTToolState ts = new CTToolState("test", runenv, tool, null);
		//
		boolean ecaught = false;
		try {
			ts.call("fail", null);
		} catch (CTToolException e) {
			assertNotNull(e);
			ecaught = true;
		}
		assertTrue(ecaught);
	}

	public void testAddGetScript() {
		CTClient c = getNewClient();
		CTTool tool = c.getObjectFactory().getTool();
		tool.addScript("testscript");
		assertNotNull(tool.getScript("testscript"));
	}
}
