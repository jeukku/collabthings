package org.collabthings.unittests;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.model.CTFactory;
import org.collabthings.model.impl.CTScriptImpl;
import org.collabthings.model.run.CTRunEnvironmentBuilder;
import org.collabthings.model.run.impl.CTRunEnvironmentBuilderImpl;

public final class TestRunEnvBuilder extends CTTestCase {

	public void testStorage() {
		CTClient c = getNewClient();
		CTRunEnvironmentBuilder b = new CTRunEnvironmentBuilderImpl(c);
		b.setName("testnameeb");

		String value = "" + Math.random();
		c.getStorage().writeToStorage("test", "test", value);
		assertEquals(value, b.readStorage("self/test/test"));
		assertEquals(value, b.readStorage(c.getClient().getService().getUser().getUsername() + "/test/test"));
	}

	public void testGetFactory() {
		CTClient c = getNewClient();
		CTRunEnvironmentBuilder b = new CTRunEnvironmentBuilderImpl(c);
		b.setName("testnameeb");

		CTFactory f = c.getObjectFactory().getFactory();

		f.addFactory();
		f.addFactory();
		f.addFactory("testname");
		f.publish();

		b.getEnvironment().setParameter("factoryid", f.getID());
		CTScriptImpl taskscript = new CTScriptImpl(c);
		// TODO create a task

		b.getEnvironment().addScript("taskscript", taskscript);
		CTScriptImpl initscript = new CTScriptImpl(c);

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

		CTClient c2 = getNewClient();
		CTRunEnvironmentBuilder b2 = c2.getObjectFactory().getRuntimeBuilder(b.getID().getStringID());
		assertNotNull(b2);
		b2 = c2.getObjectFactory().getRuntimeBuilder(b.getID().getStringID());
		assertNotNull(b2);

		assertEquals(b.getName(), b2.getName());
		CTRunEnvironment runEnvironment = b2.getRunEnvironment();
		assertNotNull(runEnvironment);

		assertNotNull(b.printOut());
	}
}
