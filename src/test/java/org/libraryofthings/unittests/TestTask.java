package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.impl.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.impl.LOTScriptRunnerImpl;
import org.libraryofthings.environment.impl.LOTTaskImpl;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.model.impl.LOTScriptImpl;
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
		assertFalse(runner.run());
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
