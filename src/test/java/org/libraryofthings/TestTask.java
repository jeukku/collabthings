package org.libraryofthings;

import java.io.IOException;

import org.libraryofthings.model.LOTTask;
import org.xml.sax.SAXException;

public final class TestTask extends LOTTestCase {

	public void testSaveAndLoad() throws IOException, SAXException {
		LOTEnvironment env = getNewEnv();
		assertNotNull(env);
		//
		LOTTask s = new LOTTask(env);
		s.setName("a task");
		s.addSubTask(new LOTTask(env));
		assertTrue(s.save());
		s.publish();
		//
		LOTEnvironment benv = getNewEnv();
		assertNotNull(benv);
		LOTTask bs = new LOTTask(benv, s.getServiceObject().getID()
				.getStringID());
		//
		assertEquals(bs.getName(), s.getName());
		assertTrue(bs.getSubTasks().size()>0);
		assertTrue(s.getSubTasks().containsAll(bs.getSubTasks()));
		assertTrue(bs.getSubTasks().containsAll(s.getSubTasks()));
	}

}
