package org.libraryofthings.integrationtests;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.math.LVector;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private static final long WAIT_A_BIT = 20;
	private static final float MOVING_LOCATION_LENGTH_TRIGGER = 0.000000001f;
	private LOTEnvironment env;
	private RunEnvironment simenv;
	private LOTToolState tool;
	private LVector targetnormal;
	private LVector targetlocation;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
	private Thread thread;
	//
	private LLog log = LLog.getLogger(this);

	public ReallySimpleSuperheroRobot(final LOTEnvironment env,
			RunEnvironment simenv) {
		this.env = env;
		this.simenv = simenv;
	}

	@Override
	public synchronized void move(LVector l, LVector n) {
		targetlocation = l.copy();
		targetnormal = n.copy();
		//
		while (simenv.isRunning()
				&& targetlocation.getSub(location).length() > MOVING_LOCATION_LENGTH_TRIGGER) {
			waitAWhile();
		}
	}

	@Override
	public void setTool(LOTToolState lotToolState) {
		this.tool = lotToolState;
	}

	@Override
	public void step(double dtime) {
		if (targetlocation != null) {
			LVector distance = targetlocation.getSub(location);
			double length = distance.length();
			if (length > 0.01) {
				length = 0.01;
			}

			if (length > MOVING_LOCATION_LENGTH_TRIGGER) {
				LVector direction = distance.getNormalized();

				direction.mult(length);
				log.info("moving " + direction + " location :" + location);
				location.add(direction);
				tool.setLocation(location, targetnormal);
			}
		}
	}

	private synchronized void waitAWhile() {
		try {
			this.wait(WAIT_A_BIT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
