package org.collabthings.unittests;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.impl.LOTScriptImpl;
import org.collabthings.model.run.LOTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.LOTRunEnvironmentBuilderImpl;

public final class TestRunEnvBuilder extends LOTTestCase {

	public void testStorage() {
		LOTClient c = getNewClient();
		LOTRunEnvironmentBuilder b = new LOTRunEnvironmentBuilderImpl(c);
		b.setName("testnameeb");

		String value = "" + Math.random();
		c.getStorage().writeToStorage("test", "test", value);
		assertEquals(value, b.readStorage("self/test/test"));
		assertEquals(
				value,
				b.readStorage(c.getClient().getService().getUser()
						.getUsername()
						+ "/test/test"));
	}

	public void testGetFactory() {
		LOTClient c = getNewClient();
		LOTRunEnvironmentBuilder b = new LOTRunEnvironmentBuilderImpl(c);
		b.setName("testnameeb");

		LOTFactory f = c.getObjectFactory().getFactory();

		f.addFactory();
		f.addFactory();
		f.addFactory("testname");
		f.publish();

		b.getEnvironment().setParameter("factoryid", f.getID());
		LOTScriptImpl taskscript = new LOTScriptImpl(c);
		// TODO create a task

		b.getEnvironment().addScript("taskscript", taskscript);
		LOTScriptImpl initscript = new LOTScriptImpl(c);

		StringBuilder sb = new StringBuilder();
		sb.append("function run(eb) { ");
		sb.append("var task = eb.getEnvironment().getScript(\"taskscript\");");
		sb.append("var state = eb.createFactoryState(\"f\", eb.getEnvironment().getParameter(\"factoryid\"));");
		sb.append("state.getRunEnvironment().getEnvironment().addScript(\"task\", task);");
		sb.append("state.addTask(\"task\", null);");
		sb.append("return state.getRunEnvironment();");
		sb.append("}");

		sb.append("function info() { return 'info'; }");

		initscript.setScript(sb.toString());
		b.getEnvironment().addScript("init", initscript);

		b.publish();

		LOTClient c2 = getNewClient();
		LOTRunEnvironmentBuilder b2 = c2.getObjectFactory().getRuntimeBuilder(
				b.getID().getStringID());
		assertNotNull(b2);
		b2 = c2.getObjectFactory().getRuntimeBuilder(b.getID().getStringID());
		assertNotNull(b2);

		assertEquals(b.getName(), b2.getName());
		LOTRunEnvironment runEnvironment = b2.getRunEnvironment();
		assertNotNull(runEnvironment);

		assertNotNull(b.printOut());
	}
}
