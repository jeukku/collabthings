package org.libraryofthings.unittests;

import java.io.IOException;

import org.collabthings.LOTClient;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.environment.impl.LOTRunEnvironmentImpl;
import org.collabthings.environment.impl.LOTToolState;
import org.collabthings.environment.impl.LOTToolUser;
import org.collabthings.environment.impl.ReallySimpleSuperheroRobot;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.model.impl.LOTFactoryImpl;
import org.collabthings.model.impl.LOTToolImpl;
import org.libraryofthings.LOTTestCase;
import org.xml.sax.SAXException;

import waazdoh.common.MTimedFlag;

public final class TestSuperheroRobot extends LOTTestCase {

	private static final double MAX_DISTANCE = 0.00001;

	public void testMoveTo() throws IOException, SAXException {
		final LOTClient e = getNewClient();
		assertNotNull(e);

		LOTRunEnvironment rune = new LOTRunEnvironmentImpl(e,
				new LOTEnvironmentImpl(e));
		final ReallySimpleSuperheroRobot robot = new ReallySimpleSuperheroRobot(
				rune, null);
		LOTToolState lotToolState = new LOTToolState("test", rune,
				new LOTToolImpl(e), null);
		robot.setTool(lotToolState);

		final MTimedFlag flag = new MTimedFlag(3000);

		new Thread(() -> {
			try {
				while (rune.isRunning() && !flag.isTriggered()) {
					robot.step(10);
				}
			} finally {
				rune.stop();
				e.stop();
			}
		}).start();

		LVector targetlocation = new LVector(10, 0, 0);
		robot.move(targetlocation, new LVector(0, 1, 0), Math.PI / 2);

		assertTrue("" + targetlocation + " "
				+ robot.getOrientation().getLocation(),
				targetlocation.getSub(robot.getOrientation().getLocation())
						.length() < MAX_DISTANCE);

		rune.stop();
	}

	public void testSpawn() {
		final LOTClient c = getNewClient();
		assertNotNull(c);

		LVector spawnlocation = new LVector(10, 10, 10);

		LOTFactory f = new LOTFactoryImpl(c);
		f.setToolUserSpawnLocation(spawnlocation);

		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		LOTRunEnvironment rune = new LOTRunEnvironmentImpl(c, e);
		LOTFactoryState state = new LOTFactoryState(c, e, "test", f);
		state.addSuperheroRobot();
		assertFalse(state.getToolUsers().isEmpty());
		LOTToolUser user = state.getToolUsers().get(0);
		assertNotNull(user);
		LVector l = user.getOrientation().getLocation();
		assertReallyClose(spawnlocation, l);
	}
}
