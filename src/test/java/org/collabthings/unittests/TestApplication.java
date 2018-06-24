package org.collabthings.unittests;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.application.CTApplicationRunner;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.model.CTApplication;
import org.collabthings.model.CTEnvironment;
import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;
import org.collabthings.model.impl.CTEnvironmentImpl;

public final class TestApplication extends CTTestCase {

	private static final String THIS_SHOULD_WORK = "this should work.";
	private static final String SCRIPT_ENV_TEST_VALUE = "testvalue " + Math.random();
	private static final String FAILING_SCRIPT = "FAIL";

	public void testSaveAndLoad() {
		CTClient client = getNewClient();
		assertNotNull(client);
		//
		CTApplication s = getAndTestWorkingAppExample(client);
		s.publish();
		//
		CTClient benv = getNewClient();
		assertNotNull(benv);
		CTApplication bs = benv.getObjectFactory().getApplication(s.getID().getStringID());
		assertNotNull(bs);
		bs = benv.getObjectFactory().getApplication(s.getID().getStringID());
		assertNotNull(bs);

		assertEquals(s.getObject().toText(), bs.getObject().toText());
		assertTrue(s.isOK());
		//
		CTEnvironment env = new CTEnvironmentImpl(client);
		CTRunEnvironmentImpl runenv = new CTRunEnvironmentImpl(client, env);

		CTApplicationRunner runner = new CTApplicationRunner(bs);
		runner.run(runenv, null);

		assertEquals(SCRIPT_ENV_TEST_VALUE, runenv.getParameter("testvalue"));
	}

	private CTApplication getAndTestWorkingAppExample(CTClient env) {
		CTApplication s = getWorkingAppExample(env);
		assertNotNull(s);
		s.save();
		return s;
	}

	private CTApplication getWorkingAppExample(CTClient env) {
		CTApplication app = env.getObjectFactory().getApplication();
		ApplicationLine setline = new ApplicationLine();
		setline.put("a", "env");
		setline.put("action", "set");
		setline.put("key", "testvalue");
		setline.put("value", SCRIPT_ENV_TEST_VALUE);

		app.addApplicationLine(setline);
		return app;
	}

	private CTApplication getApplication(CTClient env, String script) {
		try {
			CTApplication s = env.getObjectFactory().getApplication();
			s.setApplication(script);
			if (s.isOK()) {
				return s;
			} else {
				return null;
			}
		} catch (ClassCastException e) {
			log.info("logged " + e);
			return null;
		}
	}

	public void testRuntimeEnvironmentParameters() {
		CTClient client = getNewClient();
		CTApplication s = getApplication(client, "lines:\n - { a: env, action: set, key: 'test', value: 'testvalue' }");

		assertNotNull(s);
		CTEnvironment env = new CTEnvironmentImpl(client);
		CTRunEnvironment e = new CTRunEnvironmentImpl(client, env);
		CTApplicationRunner runner = new CTApplicationRunner(s);

		runner.run(e, null);
		assertEquals("testvalue", e.getParameter("test"));
	}

	public void testFailLoad() {
		CTClient env = getNewClient();
		CTApplication s = getApplication(env, FAILING_SCRIPT);
		assertNull(s);
	}

}
