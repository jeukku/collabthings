package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.environment.impl.LOTScriptRunnerImpl;
import org.collabthings.environment.impl.LOTTaskImpl;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.model.impl.LOTScriptImpl;
import org.xml.sax.SAXException;

public final class TestTask extends LOTTestCase {

	public void testRun() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(c,
				new LOTEnvironmentImpl(c));

		LOTScriptImpl script = new LOTScriptImpl(c);
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(script, runenv,
				null);
		LOTTaskImpl t = new LOTTaskImpl(runner, null);
		assertTrue(t.run());
		assertNull(t.getError());
	}

	public void testFail() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(c,
				new LOTEnvironmentImpl(c));
		LOTScriptImpl script = new LOTScriptImpl(c);
		script.setScript("function info() {} function run() { foo.bar(); }");
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(script, runenv,
				null);
		LOTTaskImpl t = new LOTTaskImpl(runner, null);
		assertFalse(t.run());
		assertNotNull(t.getError());
	}

	public void testWait() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTRunEnvironmentImpl runenv = new LOTRunEnvironmentImpl(c,
				new LOTEnvironmentImpl(c));
		LOTScriptImpl script = new LOTScriptImpl(c);
		LOTScriptRunnerImpl runner = new LOTScriptRunnerImpl(script, runenv,
				null);

		LOTTaskImpl t = new LOTTaskImpl(runner, null);
		new Thread(() -> {
			t.run();
		}).start();
		//
		t.waitUntilFinished();
		assertTrue(t.isRun());
	}
}
