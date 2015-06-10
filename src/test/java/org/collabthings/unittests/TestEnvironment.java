package org.collabthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTScript;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.model.impl.LOTScriptImpl;
import org.xml.sax.SAXException;

public final class TestEnvironment extends LOTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTEnvironmentImpl orge = new LOTEnvironmentImpl(c);

		LOTScriptImpl lotScript = new LOTScriptImpl(c);
		orge.addScript("test", lotScript);
		orge.save();
		orge.publish();
		//
		LOTEnvironmentImpl newe = new LOTEnvironmentImpl(c, orge
				.getServiceObject().getID().getStringID());

		LOTScript loadedscript = newe.getScript("test");
		assertNotNull(loadedscript);
		assertEquals(lotScript.getBean().toText(), loadedscript.getBean()
				.toText());
	}

	public void testSaveAndLoad() throws IOException, SAXException,
			NoSuchMethodException, ScriptException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		String paramname = "testparam";
		e.setParameter(paramname, "testvalue");
		//
		LOTScriptImpl lotScript = new LOTScriptImpl(c);
		e.addScript("test", lotScript);
		lotScript
				.setScript("function info() { return \"testing tool script\"; }");
		//
		e.addScript("testscript", lotScript);
		//
		e.save();
		e.publish();
		//
		LOTClient bc = getNewClient();
		assertNotNull(bc);
		LOTEnvironmentImpl benv = new LOTEnvironmentImpl(c, e
				.getServiceObject().getID().getStringID());
		assertEquals(e.getServiceObject().getBean().toText(), benv
				.getServiceObject().getBean().toText());
		assertNotNull(benv.getParameter(paramname));
		assertEquals(e.getParameter(paramname), benv.getParameter(paramname));
	}

	public void testAddGetScript() throws IOException, SAXException {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl env = new LOTEnvironmentImpl(c);
		assertNull(env.getScript("FAIL"));
		//
		String scriptname = "testscript";
		env.addScript(scriptname, new LOTScriptImpl(c));
		assertNotNull(env.getScript(scriptname));
	}

	public void testAddGetParameter() {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		String testparam = "testparam";
		String testvalue = "testvalue";
		e.setParameter(testparam, testvalue);
		assertEquals(testvalue, e.getParameter(testparam));
	}

	public void testAddSaveGetTool() {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		String testtool = "testtool";
		e.addTool(testtool, c.getObjectFactory().getTool());
		e.save();
		LOTEnvironmentImpl benv = new LOTEnvironmentImpl(c, e.getID()
				.getStringID());
		assertNotNull(benv.getTool(testtool));
		assertEquals(e.getTool(testtool), benv.getTool(testtool));
	}

	public void testVectorParameters() {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		LVector LVector3d = new LVector(1, 1, 1);
		String vname = "test";
		e.setVectorParameter(vname, LVector3d);
		e.save();
		LOTEnvironmentImpl benv = new LOTEnvironmentImpl(c, e.getID()
				.getStringID());
		assertNotNull(benv.getVectorParameter(vname));
		assertEquals(LVector3d, benv.getVectorParameter(vname));

	}
}
