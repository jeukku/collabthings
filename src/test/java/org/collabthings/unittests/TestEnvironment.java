package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.model.CTApplication;
import org.collabthings.model.impl.CTApplicationImpl;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.omg.CORBA.portable.ApplicationException;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

public final class TestEnvironment extends CTTestCase {

	public void testGetAgain() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTEnvironmentImpl orge = new CTEnvironmentImpl(c);

		CTApplicationImpl ctApplication = new CTApplicationImpl(c);
		orge.addApplication("test", ctApplication);
		orge.save();
		orge.publish();
		//
		CTEnvironmentImpl newe = new CTEnvironmentImpl(c, orge.getServiceObject().getID().getStringID());

		CTApplication loadedscript = newe.getApplication("test");
		assertNotNull(loadedscript);
		assertEquals(ctApplication.getObject().toYaml(), loadedscript.getObject().toYaml());
	}

	public void testSaveAndLoad() throws IOException, SAXException, NoSuchMethodException, ApplicationException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		String paramname = "testparam";
		e.setParameter(paramname, "testvalue");
		//
		CTApplicationImpl ctApplication = new CTApplicationImpl(c);
		e.addApplication("test", ctApplication);
		//
		e.addApplication("testscript", ctApplication);
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

	public void testAddGetApplication() throws IOException, SAXException {
		CTClient c = getNewClient();
		CTEnvironmentImpl env = new CTEnvironmentImpl(c);
		assertNull(env.getApplication("FAIL"));
		//
		String scriptname = "testscript";
		env.addApplication(scriptname, new CTApplicationImpl(c));
		assertNotNull(env.getApplication(scriptname));

		env.renameApplication("testscript", "testscript2");
		assertNull(env.getApplication("testscript"));
		assertNotNull(env.getApplication("testscript2"));

		env.deleteApplication("testscript2");
		assertNull(env.getApplication("testscript2"));
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
