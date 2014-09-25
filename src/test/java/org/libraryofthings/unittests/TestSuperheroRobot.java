package org.libraryofthings.unittests;

import java.io.IOException;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.LOTRunEnvironmentImpl;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.ReallySimpleSuperheroRobot;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;
import org.libraryofthings.model.impl.LOTToolImpl;
import org.xml.sax.SAXException;

import waazdoh.util.MTimedFlag;

public final class TestSuperheroRobot extends LOTTestCase {

	private static final double MAX_DISTANCE = 0.00001;

	public void testMoveTo() throws IOException, SAXException {
		final LOTClient e = getNewClient();
		assertNotNull(e);

		RunEnvironment rune = new LOTRunEnvironmentImpl(e,
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
		robot.move(targetlocation, new LVector(0, 1, 0));

		assertTrue(
				"" + targetlocation + " " + robot.getLocation(),
				targetlocation.getSub(robot.getLocation()).length() < MAX_DISTANCE);

		rune.stop();
	}
}
