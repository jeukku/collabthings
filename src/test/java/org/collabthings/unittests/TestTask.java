package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.application.CTApplicationRunner;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.environment.impl.CTScriptRunnerImpl;
import org.collabthings.environment.impl.CTTaskImpl;
import org.collabthings.model.CTApplication;
import org.collabthings.model.impl.CTApplicationImpl;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTScriptImpl;
import org.xml.sax.SAXException;

public final class TestTask extends CTTestCase {

	public void testRun() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(c, new CTEnvironmentImpl(c));

		CTApplication app = new CTApplicationImpl(c);

		CTTaskImpl t = new CTTaskImpl(new CTApplicationRunner(app), runenv);
		assertTrue(t.run());
		assertNull(t.getError());
	}

	public void testFail() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(c, new CTEnvironmentImpl(c));
		CTApplication app = new CTApplicationImpl(c);
		ApplicationLine applicationLine = new ApplicationLine();
		applicationLine.put("a", "FAIL");
		app.addApplicationLine(applicationLine);
		CTTaskImpl t = new CTTaskImpl(new CTApplicationRunner(app), runenv);
		try {
			assertFalse(t.run());
		} catch (RuntimeException e) {
			assertNotNull(e);
		}
		assertNotNull(t.getError());
	}

	public void testWait() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(c, new CTEnvironmentImpl(c));
		CTApplication app = new CTApplicationImpl(c);

		CTTaskImpl t = new CTTaskImpl(new CTApplicationRunner(app), runenv);
		new Thread(() -> {
			t.run();
		}).start();
		//
		t.waitUntilFinished();
		assertTrue(t.isRun());
	}
}
