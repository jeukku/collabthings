package org.collabthings.environment.impl;

import org.collabthings.LOTToolException;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.LOTRuntimeEvent;
import org.collabthings.environment.SimulationView;
import org.collabthings.math.LOrientation;
import org.collabthings.math.LTransformation;
import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTValues;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private static final long WAIT_A_BIT = 20;
	private static final float MOVING_orientation_LENGTH_TRIGGER = 0.000000001f;
	private static final double orientation_PRINTOUT = 20000;
	//
	final private LOTRunEnvironment simenv;
	private LOTToolState tool;

	private final LOrientation orientation = new LOrientation();
	private final LOrientation targetorientation = new LOrientation();

	//
	private LLog log = LLog.getLogger(this);
	//
	private double orientationprintouttimer = 0;
	private double speed = 10;
	final private LOTFactoryState factory;
	private LOTEvents events = new LOTEvents();
	private long movestarttime;
	private boolean stopped;

	public ReallySimpleSuperheroRobot(LOTRunEnvironment simenv,
			LOTFactoryState factory, LVector norientation) {
		this.simenv = simenv;
		this.factory = factory;
		if (norientation != null) {
			this.orientation.getLocation().set(norientation.copy());
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
	public PrintOut printOut() {
		PrintOut p = new PrintOut();
		p.append("superherorobot at " + orientation);
		return p;
	}

	@Override
	public String toString() {
		return "SuperHeroRobot[" + super.hashCode() + "][" + tool + "]["
				+ orientation + "]";
	}

	@Override
	public LOTToolState getTool() {
		return tool;
	}

	@Override
	public synchronized void move(LVector l, LVector n, double angle) {
		targetorientation.getLocation().set(l);
		events.add(new LOTRuntimeEvent(this, "Moving to " + targetorientation,
				null));

		movestarttime = System.currentTimeMillis();

		targetorientation.getNormal().set(n);
		targetorientation.setAngle(angle);
		log("Target orientation " + targetorientation);
		log("factory " + this.factory);
		//
		while (simenv.isRunning()
				&& !stopped
				&& targetorientation.getLocation()
						.getSub(orientation.getLocation()).length() > MOVING_orientation_LENGTH_TRIGGER) {
			waitAWhile();
		}

		if (!stopped) {
			tool.setOrientation(targetorientation.getLocation(),
					targetorientation.getNormal(), targetorientation.getAngle());

			log("Moved to " + targetorientation + " " + orientation + " in "
					+ (System.currentTimeMillis() - movestarttime) + "ms");
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
		log("setting tool " + lotToolState);
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
	public LOrientation getOrientation() {
		return orientation;
	}

	@Override
	public LTransformation getTransformation() {
		return new LTransformation(orientation);
	}

	@Override
	public synchronized void stop() {
		stopped = true;
	}

	@Override
	public synchronized void step(double dtime) {
		orientationprintouttimer += dtime;

		if (targetorientation != null) {
			LVector vdistance = targetorientation.getLocation().getSub(
					orientation.getLocation());
			double distance = vdistance.length();
			// log("step " + dtime + " l:" + orientation + " distance:" +
			// distance);

			double dangle = targetorientation.getAngle()
					- orientation.getAngle();
			dangle *= dtime * speed;
			orientation.setAngle(orientation.getAngle() + dangle);

			LVector nnormal = targetorientation.getNormal().getNormalized();

			// slow moving if orientation is not right
			double normaldot = nnormal.dot(orientation.getNormal());
			if (normaldot < -0.9) {
				nnormal.x += nnormal.y;
				nnormal.y += nnormal.z;
				nnormal.z += nnormal.x;
				nnormal.normalize();
				normaldot = nnormal.dot(orientation.getNormal());
			}

			if (normaldot < 0.1) {
				distance *= normaldot;
			}

			nnormal.scale(dtime * speed);

			orientation.getNormal().add(nnormal);
			orientation.getNormal().normalize();

			double maxdistance = dtime * speed;
			if (vdistance.length() < maxdistance) {
				// just move to right direction
				double scale = 0.8;
				if (distance < maxdistance / 10) {
					// close enough, just go there.
					scale = 1;
				}
				LVector scaled = vdistance.getScaled(scale);
				orientation.getLocation().add(scaled);
			} else if (distance > MOVING_orientation_LENGTH_TRIGGER) {
				distance = maxdistance;

				// this is a bit random, but not going straight from a to b.
				LVector direction = vdistance.getNormalized();
				double ddot = direction.dot(targetorientation.getNormal());
				if (ddot > 0) {
					direction.add(new LVector(direction.y, direction.z,
							direction.x));
				} else if (ddot > -0.99) {
					LVector cross = new LVector();
					cross.cross(direction, targetorientation.getNormal());
					direction.add(cross);
				}

				direction.normalize();
				direction.scale(distance);

				orientation.getLocation().add(direction);
				if (tool != null) {
					tool.setOrientation(orientation.getLocation(),
							orientation.getNormal(), orientation.getAngle());
				}
			} else {
				log("Distance less than trigger length");
				this.notifyAll();
			}

		}

		if (orientationprintouttimer > orientation_PRINTOUT) {
			debugInfo(dtime);
			orientationprintouttimer = 0;
		}
	}

	private void log(String string) {
		log.info("" + (System.currentTimeMillis() - movestarttime) + "ms -- "
				+ string);
	}

	private void debugInfo(double dtime) {
		log("tool:" + tool + " orientation " + orientation + " step:" + dtime
				+ " targetorientation:" + targetorientation);
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
