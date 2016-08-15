package org.collabthings.unittests;

import java.io.IOException;

import javax.script.ScriptException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTScript;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTScriptImpl;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

public final class TestEnvironment extends CTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTEnvironmentImpl orge = new CTEnvironmentImpl(c);

		CTScriptImpl ctScript = new CTScriptImpl(c);
		orge.addScript("test", ctScript);
		orge.save();
		orge.publish();
		//
		CTEnvironmentImpl newe = new CTEnvironmentImpl(c, orge.getServiceObject().getID().getStringID());

		CTScript loadedscript = newe.getScript("test");
		assertNotNull(loadedscript);
		assertEquals(ctScript.getObject().toYaml(), loadedscript.getObject().toYaml());
	}

	public void testSaveAndLoad() throws IOException, SAXException, NoSuchMethodException, ScriptException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		String paramname = "testparam";
		e.setParameter(paramname, "testvalue");
		//
		CTScriptImpl ctScript = new CTScriptImpl(c);
		e.addScript("test", ctScript);
		ctScript.setScript("function info() { return \"testing tool script\"; }");
		//
		e.addScript("testscript", ctScript);
		//
		e.save();
		e.publish();
		//
		CTClient bc = getNewClient();
		assertNotNull(bc);
		CTEnvironmentImpl benv = new CTEnvironmentImpl(c, e.getServiceObject().getID().getStringID());
		assertEquals(e.getServiceObject().getBean().toYaml(), benv.getServiceObject().getBean().toYaml());
		assertNotNull(benv.getParameter(paramname));
		assertEquals(e.getParameter(paramname), benv.getParameter(paramname));
	}

	public void testAddGetScript() throws IOException, SAXException {
		CTClient c = getNewClient();
		CTEnvironmentImpl env = new CTEnvironmentImpl(c);
		assertNull(env.getScript("FAIL"));
		//
		String scriptname = "testscript";
		env.addScript(scriptname, new CTScriptImpl(c));
		assertNotNull(env.getScript(scriptname));

		env.renameScript("testscript", "testscript2");
		assertNull(env.getScript("testscript"));
		assertNotNull(env.getScript("testscript2"));

		env.deleteScript("testscript2");
		assertNull(env.getScript("testscript2"));
	}

	public void testAddGetParameter() {
		CTClient c = getNewClient();
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		String testparam = "testparam";
		String testvalue = "testvalue";
		e.setParameter(testparam, testvalue);
		assertEquals(testvalue, e.getParameter(testparam));
	}

	public void testAddSaveGetTool() {
		CTClient c = getNewClient();
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		String testtool = "testtool";
		e.addTool(testtool, c.getObjectFactory().getTool());
		e.publish();
		CTEnvironmentImpl benv = new CTEnvironmentImpl(c, e.getID().getStringID());
		assertNotNull(benv.getTool(testtool));
		assertEquals(e.getTool(testtool), benv.getTool(testtool));

		benv.renameTool("testtool", "testtool2");
		assertNull(benv.getTool("testtool"));
		assertNotNull(benv.getTool("testtool2"));

		benv.deleteTool("testtool2");
		assertNull(benv.getTool("testtool2"));
	}

	public void testVectorParameters() {
		CTClient c = getNewClient();
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		Vector3f Vector3f3d = new Vector3f(1, 1, 1);
		String vname = "test";
		e.setVectorParameter(vname, Vector3f3d);
		e.save();
		CTEnvironmentImpl benv = new CTEnvironmentImpl(c, e.getID().getStringID());
		assertNotNull(benv.getVectorParameter(vname));
		assertEquals(Vector3f3d, benv.getVectorParameter(vname));

	}
}
