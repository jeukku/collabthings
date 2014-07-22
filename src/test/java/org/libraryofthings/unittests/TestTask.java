package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.LOTTask;
import org.libraryofthings.model.LOTEnvironmentImpl;
import org.libraryofthings.model.LOTScript;
import org.xml.sax.SAXException;

public final class TestTask extends LOTTestCase {

	public void testRun() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTScript script = new LOTScript(c);
		LOTTask t = new LOTTask(script, null);
		assertTrue(t
				.run(new LOTRunEnvironmentImpl(c, new LOTEnvironmentImpl(c))));
	}

	public void testFail() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTScript script = new LOTScript(c);
		script.setScript("function info() {} function run() { foo.bar(); }");
		LOTTask t = new LOTTask(script, null);
		assertFalse(t
				.run(new LOTRunEnvironmentImpl(c, new LOTEnvironmentImpl(c))));
	}

	public void testWait() throws IOException, SAXException {
		LOTClient c = getNewClient();
		assertNotNull(c);
		//
		LOTScript script = new LOTScript(c);
		LOTTask t = new LOTTask(script, null);
		new Thread(() -> {
			t.run(new LOTRunEnvironmentImpl(c, new LOTEnvironmentImpl(c)));
		}).start();
		//
		t.waitUntilFinished();
		assertTrue(t.isRun());
	}
}
