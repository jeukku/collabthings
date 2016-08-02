package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.environment.impl.CTScriptRunnerImpl;
import org.collabthings.environment.impl.CTTaskImpl;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTScriptImpl;
import org.xml.sax.SAXException;

public final class TestTask extends CTTestCase {

	public void testRun() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(c, new CTEnvironmentImpl(c));

		CTScriptImpl script = new CTScriptImpl(c);
		CTScriptRunnerImpl runner = new CTScriptRunnerImpl(script, runenv, null);
		CTTaskImpl t = new CTTaskImpl(runner, null);
		assertTrue(t.run());
		assertNull(t.getError());
	}

	public void testFail() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(c, new CTEnvironmentImpl(c));
		CTScriptImpl script = new CTScriptImpl(c);
		script.setScript("function info() {} function run() { foo.bar(); }");
		CTScriptRunnerImpl runner = new CTScriptRunnerImpl(script, runenv, null);
		CTTaskImpl t = new CTTaskImpl(runner, null);
		assertFalse(t.run());
		assertNotNull(t.getError());
	}

	public void testWait() throws IOException, SAXException {
		CTClient c = getNewClient();
		assertNotNull(c);
		//
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(c, new CTEnvironmentImpl(c));
		CTScriptImpl script = new CTScriptImpl(c);
		CTScriptRunnerImpl runner = new CTScriptRunnerImpl(script, runenv, null);

		CTTaskImpl t = new CTTaskImpl(runner, null);
		new Thread(() -> {
			t.run();
		}).start();
		//
		t.waitUntilFinished();
		assertTrue(t.isRun());
	}
}
