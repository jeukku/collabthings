package org.libraryofthings;

import java.io.IOException;

import org.libraryofthings.model.LOTTask;
import org.xml.sax.SAXException;

public final class TestTask extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOTTask s = env.getObjectFactory().getTask();
		s.setName("a task");
		s.addSubTask(env.getObjectFactory().getTask());
		//
		assertTrue(s.publish());
		//
		assertEquals(
				s,
				env.getObjectFactory().getTask(
						s.getServiceObject().getID().getStringID()));
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOTTask bs = benv.getObjectFactory().getTask(
				s.getServiceObject().getID().getStringID());
		//
		assertEquals(bs.getName(), s.getName());
		assertTrue(bs.getSubTasks().size() > 0);
		assertTrue(s.getSubTasks().containsAll(bs.getSubTasks()));
		assertTrue(bs.getSubTasks().containsAll(s.getSubTasks()));
		//
		assertEquals(
				bs,
				env.getObjectFactory().getTask(
						bs.getServiceObject().getID().getStringID()));
	}

}
