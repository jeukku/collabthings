package org.collabthings.unittests;

import java.io.IOException;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.environment.impl.CTRunEnvironmentImpl;
import org.collabthings.environment.impl.CTToolState;
import org.collabthings.environment.impl.CTToolUser;
import org.collabthings.environment.impl.ReallySimpleSuperheroRobot;
import org.collabthings.model.CTFactory;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.model.impl.CTFactoryImpl;
import org.collabthings.model.impl.CTToolImpl;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

import collabthings.core.utils.WTimedFlag;

public final class TestSuperheroRobot extends CTTestCase {

	private static final double MAX_DISTANCE = 0.00001;

	public void testMoveTo() throws IOException, SAXException {
		final CTClient e = getNewClient();
		assertNotNull(e);

		CTRunEnvironment rune = new CTRunEnvironmentImpl(e, new CTEnvironmentImpl(e));
		final ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(rune, null);
		CTToolState ctToolState = new CTToolState("test", rune, new CTToolImpl(e), null);
		robot.setTool(ctToolState);

		final WTimedFlag flag = new WTimedFlag(3000000);

		new Thread(() -> {
			try {
				while (rune.isRunning() && !flag.isTriggered()) {
					robot.step(0.10);
				}
			} finally {
				rune.stop();
				e.stop();
			}
		}).start();

		Vector3f targetlocation = new Vector3f(10, 0, 0);
		robot.move(targetlocation, new Vector3f(0, 1, 0), Math.PI / 2);

		assertTrue("" + targetlocation + " " + robot.getOrientation().getLocation(),
				targetlocation.subtract(robot.getOrientation().getLocation()).length() < MAX_DISTANCE);

		rune.stop();
	}

	public void testSpawn() {
		final CTClient c = getNewClient();
		assertNotNull(c);

		Vector3f spawnlocation = new Vector3f(10, 10, 10);

		CTFactory f = new CTFactoryImpl(c);
		f.setToolUserSpawnLocation(spawnlocation);

		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		CTFactoryState state = new CTFactoryState(c, e, "test", f);
		state.addSuperheroRobot();
		assertFalse(state.getToolUsers().isEmpty());
		CTToolUser user = state.getToolUsers().get(0);
		assertNotNull(user);
		Vector3f l = user.getOrientation().getLocation();
		assertReallyClose(spawnlocation, l);
	}
}
