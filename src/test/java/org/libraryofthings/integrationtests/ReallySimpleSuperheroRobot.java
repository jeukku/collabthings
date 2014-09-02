package org.libraryofthings.integrationtests;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private static final long WAIT_A_BIT = 20;
	private static final float MOVING_LOCATION_LENGTH_TRIGGER = 0.000000001f;
	private static final double LOCATION_PRINTOUT = 2000;
	//
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
	private double speed = 1;
	private LOTFactoryState factorystate;

	private static int counter = 0;

	public ReallySimpleSuperheroRobot(RunEnvironment simenv) {
		ReallySimpleSuperheroRobot.counter++;
		this.simenv = simenv;
	}

	@Override
	public String toString() {
		return "SuperHeroRobot[" + ReallySimpleSuperheroRobot.counter + "]["
				+ tool + "][" + location + "]";
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
		tool.setLocation(targetlocation, targetnormal);

		log.info("Moved to " + targetlocation + " " + location);
	}

	@Override
	public void setTool(LOTToolState lotToolState) {
		log.info("setting tool " + lotToolState);
		this.tool = lotToolState;
		initLogger();
	}

	@Override
	public void setParentFactory(LOTFactoryState nfactorystate) {
		this.factorystate = nfactorystate;
	}

	private void initLogger() {
		log = LLog.getLogger(this);
	}

	@Override
	public LVector getLocation() {
		return location.copy();
	}

	@Override
	public LVector getAbsoluteLocation() {
		LVector ret = new LVector();
		if (factorystate != null) {
			ret.add(factorystate.getAbsoluteLocation());
		}

		ret.add(location);
		return ret;
	}

	@Override
	public void stop() {
		targetlocation = null;
	}

	@Override
	public void step(double dtime) {
		locationprintouttimer += dtime;

		if (targetlocation != null) {
			LVector vdistance = targetlocation.getSub(location);
			double distance = vdistance.length();
			double maxdistance = dtime * speed / 1000;
			if (distance > maxdistance) {
				distance = maxdistance;
			}

			// checking distance because we cannot normalize zero length vector
			if (distance > MOVING_LOCATION_LENGTH_TRIGGER) {
				LVector direction = vdistance.getNormalized();

				direction.mult(distance);
				location.add(direction);
				tool.setLocation(location, targetnormal);
			}
		}

		if (locationprintouttimer > LOCATION_PRINTOUT) {
			log.info("tool:" + tool + " location " + location + " normal "
					+ normal + " step:" + dtime + " targetlocation:"
					+ targetlocation + " abslocation:" + getAbsoluteLocation());
			locationprintouttimer = 0;
		}
	}

	private synchronized void waitAWhile() {
		try {
			this.wait(WAIT_A_BIT);
		} catch (InterruptedException e) {
			log.error(this, "waitAWhile", e);
		}
	}

	@Override
	public boolean isAvailable(LOTToolState toolstate) {
		return this.tool == toolstate || tool == null;
	}
}
