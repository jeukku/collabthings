package org.collabthings.unittests;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRunEnvironmentBuilder;
import org.collabthings.environment.impl.LOTRunEnvironmentBuilderImpl;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.impl.LOTScriptImpl;

public final class TestRunEnvBuilder extends LOTTestCase {

	public void testGetFactory() {
		LOTClient c = getNewClient();

		LOTFactory f = c.getObjectFactory().getFactory();

		f.addFactory();
		f.addFactory();
		f.addFactory("testname");
		f.publish();

		LOTRunEnvironmentBuilder b = new LOTRunEnvironmentBuilderImpl(c);
		b.getEnvironment().setParameter("factoryid", f.getID());
		LOTScriptImpl taskscript = new LOTScriptImpl(c);
		StringBuilder tasksb = new StringBuilder();
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

		LOTRunEnvironment runEnvironment = b2.getRunEnvironment();
		assertNotNull(runEnvironment);

		assertNotNull(b.printOut());
	}
}
