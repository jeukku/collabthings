package org.collabthings.environment.impl;

import org.collabthings.LLog;
import org.collabthings.LOTToolException;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.SimulationView;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTValues;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private static final long WAIT_A_BIT = 20;
	private static final float MOVING_LOCATION_LENGTH_TRIGGER = 0.000000001f;
	private static final double LOCATION_PRINTOUT = 20000;
	//
	final private LOTRunEnvironment simenv;
	private LOTToolState tool;
	private LVector targetorientationnormal;
	private double targetorientationangle;
	private LVector targetlocation;
	private LVector location = new LVector();
	private LVector orientationnormal = new LVector(1, 0, 0);
	private double orientationangle;
	//
	private LLog log = LLog.getLogger(this);
	//
	private double locationprintouttimer = 0;
	private double speed = 10;
	final private LOTFactoryState factory;
	private LOTEvents events = new LOTEvents();

	public ReallySimpleSuperheroRobot(LOTRunEnvironment simenv,
			LOTFactoryState factory, LVector nlocation) {
		this.simenv = simenv;
		this.factory = factory;
		if (nlocation != null) {
			this.location = nlocation.copy();
		}
	}

	public ReallySimpleSuperheroRobot(LOTRunEnvironment simenv,
			LOTFactoryState factory) {
		this.simenv = simenv;
		this.factory = factory;
	}

	@Override
	public String getName() {
		String name = getParameter("name");
		if (name == null) {
			name = "SuperHeroRobot";
		}
		return name;
	}

	@Override
	public String toString() {
		return "SuperHeroRobot[" + super.hashCode() + "][" + tool + "]["
				+ location + "]";
	}

	@Override
	public LOTToolState getTool() {
		return tool;
	}

	@Override
	public synchronized void move(LVector l, LVector n, double angle) {
		targetlocation = l.copy();
		events.add(new LOTRuntimeEvent(this, "Moving to " + targetlocation,
				null));

		targetorientationnormal = n.copy();
		targetorientationangle = angle;
		log.info("Target location " + targetlocation);
		log.info("factory " + this.factory);
		//
		while (simenv.isRunning()
				&& targetlocation != null
				&& targetlocation.getSub(location).length() > MOVING_LOCATION_LENGTH_TRIGGER) {
			waitAWhile();
		}

		if (targetlocation != null) {
			tool.setOrientation(targetlocation, targetorientationnormal,
					targetorientationangle);

			log.info("Moved to " + targetlocation + " " + location);
		} else {
			log.info("No target location. Propably stopped.");
		}
	}

	@Override
	public void setTool(LOTToolState lotToolState) {
		String name;
		if (tool != null) {
			name = tool.getName();
		} else {
			name = null;
		}

		events.add(new LOTRuntimeEvent(this, "set tool " + name, null));
		log.info("setting tool " + lotToolState);
		this.tool = lotToolState;
		initLogger();
	}

	@Override
	public String getParameter(String name) {
		return factory.getParameter(name);
	}

	private void initLogger() {
		log = LLog.getLogger(this);
	}

	@Override
	public LVector getLocation() {
		return location.copy();
	}

	@Override
	public LTransformation getTransformation() {
		return new LTransformation(location, orientationnormal,
				this.orientationangle);
	}

	@Override
	public LTransformation getLocationTransformation() {
		return LTransformation.getTranslate(location);
	}

	@Override
	public synchronized void stop() {
		targetlocation = null;
	}

	@Override
	public synchronized void step(double dtime) {
		locationprintouttimer += dtime;

		if (targetlocation != null) {
			LVector vdistance = targetlocation.getSub(location);
			double distance = vdistance.length();
			double maxdistance = dtime * speed;
			if (distance > maxdistance) {
				distance = maxdistance;
			}

			double dangle = targetorientationangle - orientationangle;
			dangle *= dtime * speed;
			orientationangle += dangle;

			LVector nnormal = targetorientationnormal.getNormalized();

			// slow moving if orientation is not right
			double normaldot = nnormal.dot(orientationnormal);
			if (normaldot < -0.9) {
				nnormal.x += nnormal.y;
				nnormal.y += nnormal.z;
				nnormal.z += nnormal.x;
				nnormal.normalize();
				normaldot = nnormal.dot(orientationnormal);
			}

			if (normaldot < 0) {
				normaldot = 0.1;
			}
			distance *= normaldot;

			nnormal.scale(dtime * speed);

			orientationnormal.add(nnormal);
			orientationnormal.normalize();

			// checking distance because we cannot normalize zero length vector
			if (distance > MOVING_LOCATION_LENGTH_TRIGGER) {
				// this is a bit random, but not going straight from a to b.
				LVector direction = vdistance.getNormalized();
				double ddot = direction.dot(targetorientationnormal);
				if (ddot > 0) {
					direction.add(new LVector(direction.y, direction.z,
							direction.x));
				} else if (ddot > -0.99) {
					LVector cross = new LVector();
					cross.cross(direction, targetorientationnormal);
					direction.add(cross);
				}

				direction.normalize();

				direction.scale(distance);
				location.add(direction);
				tool.setOrientation(location, orientationnormal,
						orientationangle);
			}

		}

		if (locationprintouttimer > LOCATION_PRINTOUT) {
			debugInfo(dtime);
			locationprintouttimer = 0;
		}
	}

	private void debugInfo(double dtime) {
		log.info("tool:" + tool + " location " + location + " normal "
				+ orientationnormal + " step:" + dtime + " targetlocation:"
				+ targetlocation);
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

	@Override
	public void callDraw(SimulationView view, LTransformationStack tstack)
			throws LOTToolException {
		if (tool != null) {
			LOTValues values = new LOTValues("view", view, "tstack", tstack);
			this.tool.call("draw", values);
		}
	}

	@Override
	public LOTEvents getEvents() {
		return this.events;
	}
}
