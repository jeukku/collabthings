package org.libraryofthings.integrationtests;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private static final long WAIT_A_BIT = 20;
	private static final float MOVING_LOCATION_LENGTH_TRIGGER = 0.000000001f;
	private static final double LOCATION_PRINTOUT = 2000;
	//
	private LOTEnvironment env;
	private RunEnvironment simenv;
	private LOTToolState tool;
	private LVector targetnormal;
	private LVector targetlocation;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
	//
	private LLog log = LLog.getLogger(this);
	//
	private double locationprintouttimer = 0;

	public ReallySimpleSuperheroRobot(final LOTEnvironment env,
			RunEnvironment simenv) {
		this.env = env;
		this.simenv = simenv;
	}

	@Override
	public synchronized void move(LVector l, LVector n) {
		targetlocation = l.copy();
		targetnormal = n.copy();
		log.info("Target location " + targetlocation);
		//
		while (simenv.isRunning()
				&& targetlocation.getSub(location).length() > MOVING_LOCATION_LENGTH_TRIGGER) {
			waitAWhile();
		}
		log.info("Moved to " + targetlocation + " " + location);
	}

	@Override
	public void setTool(LOTToolState lotToolState) {
		this.tool = lotToolState;
	}

	@Override
	public void step(double dtime) {
		locationprintouttimer += dtime;

		if (targetlocation != null) {
			LVector distance = targetlocation.getSub(location);
			double length = distance.length();
			if (length > 0.01) {
				length = 0.01;
			}

			if (length > MOVING_LOCATION_LENGTH_TRIGGER) {
				LVector direction = distance.getNormalized();

				direction.mult(length);
				location.add(direction);
				tool.setLocation(location, targetnormal);
			}
		}

		if (locationprintouttimer > LOCATION_PRINTOUT) {
			log.info("location " + location + " normal " + normal + " step:"
					+ dtime + " targetlocation:" + targetlocation);
			locationprintouttimer = 0;
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
