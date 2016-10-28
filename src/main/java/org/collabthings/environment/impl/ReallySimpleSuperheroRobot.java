package org.collabthings.environment.impl;

import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.CTRuntimeEvent;
import org.collabthings.math.LOrientation;
import org.collabthings.util.LLog;
import org.collabthings.util.PrintOut;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public class ReallySimpleSuperheroRobot implements CTToolUser {

	private static final double STRANGE_CONTROL_DIRECTION_DOTPRODUCT_ONE = 0.9;
	private static final double STRANGE_CONTROL_DIRECTION_DOTPRODUCT_TWO = 0.1;
	private static final long WAIT_A_BIT = 20;
	private static final double MOVING_ORIENTATION_LENGTH_TRIGGER = 0.000000001;
	private static final double ORIENTATION_PRINTOUT = 2000;
	//
	private final CTRunEnvironment simenv;
	private CTToolState tool;

	private final LOrientation orientation = new LOrientation();
	private final LOrientation targetorientation = new LOrientation();

	//
	private LLog log = LLog.getLogger(this);
	//
	private double orientationprintouttimer;
	private double speed = 10;
	private final CTFactoryState factory;
	private CTEvents events = new CTEvents();
	private long movestarttime;
	private boolean stopped;

	public ReallySimpleSuperheroRobot(CTRunEnvironment simenv, CTFactoryState factory, Vector3f norientation) {
		this.simenv = simenv;
		this.factory = factory;
		if (norientation != null) {
			this.orientation.getLocation().set(norientation.clone());
		}
	}

	public ReallySimpleSuperheroRobot(CTRunEnvironment simenv, CTFactoryState factory) {
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
		return "SuperHeroRobot[" + super.hashCode() + "][" + tool + "][" + orientation + "]";
	}

	@Override
	public CTToolState getTool() {
		return tool;
	}

	@Override
	public synchronized void move(Vector3f l, Vector3f n, double angle) {
		targetorientation.getLocation().set(l);
		events.add(new CTRuntimeEvent(this, "Moving to " + targetorientation, null));

		movestarttime = System.currentTimeMillis();

		targetorientation.getNormal().set(n);
		targetorientation.setAngle(angle);
		log("Target orientation " + targetorientation);
		log("factory " + this.factory);
		float distance;
		//
		do {
			try {
				this.wait(WAIT_A_BIT);
			} catch (InterruptedException e) {
				log.error(this, "waitAWhile", e);
				Thread.currentThread().interrupt();
			}
			distance = targetorientation.getLocation().subtract(orientation.getLocation()).length();
		} while (simenv.isRunning() && !stopped && distance > MOVING_ORIENTATION_LENGTH_TRIGGER);

		if (!stopped) {
			tool.setOrientation(targetorientation.getLocation(), targetorientation.getNormal(),
					targetorientation.getAngle());

			log("Moved to " + targetorientation + " " + orientation + " in "
					+ (System.currentTimeMillis() - movestarttime) + "ms");
		}
	}

	@Override
	public void setTool(CTToolState ctToolState) {
		String name;
		if (tool != null) {
			name = tool.getName();
		} else {
			name = null;
		}

		events.add(new CTRuntimeEvent(this, "set tool " + name, null));
		log("setting tool " + ctToolState);
		this.tool = ctToolState;
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
	public Transform getTransformation() {
		return orientation.getTransformation();
	}

	@Override
	public synchronized void stop() {
		stopped = true;
	}

	@Override
	public synchronized void step(double dtime) {
		orientationprintouttimer += dtime;
		move((float) dtime);
		printOut((float) dtime);
	}

	private synchronized void move(double dtime) {
		Vector3f vdistance = targetorientation.getLocation().subtract(orientation.getLocation());
		double distance = vdistance.length();

		double dangle = (float) (targetorientation.getAngle() - orientation.getAngle());
		dangle *= dtime * speed;
		orientation.setAngle(orientation.getAngle() + dangle);

		Vector3f nnormal = targetorientation.getNormal().normalize();

		// slow moving if orientation is not right
		double normaldot = nnormal.dot(orientation.getNormal());
		if (normaldot < -STRANGE_CONTROL_DIRECTION_DOTPRODUCT_ONE) {
			nnormal.x += nnormal.y;
			nnormal.y += nnormal.z;
			nnormal.z += nnormal.x;
			nnormal.normalize();
			normaldot = nnormal.dot(orientation.getNormal());
		}

		if (normaldot < STRANGE_CONTROL_DIRECTION_DOTPRODUCT_TWO) {
			distance *= normaldot;
		}

		nnormal.multLocal((float) (dtime * speed));

		Vector3f n = orientation.getNormal().add(nnormal);
		n = n.normalize();
		orientation.getNormal().set(n);

		double maxdistance = dtime * speed;
		if (distance > MOVING_ORIENTATION_LENGTH_TRIGGER) {
			if (vdistance.length() < maxdistance) {
				moveToTarget(vdistance, distance, maxdistance);
			} else {
				move(vdistance, maxdistance);
			}
		} else {
			this.notifyAll();
		}
	}

	private void move(Vector3f vdistance, double maxdistance) {
		double distance;
		distance = maxdistance;

		// this is a bit random, but not going straight from a to b.
		Vector3f direction = vdistance.normalize();
		double ddot = direction.dot(targetorientation.getNormal());
		if (ddot > 0) {
			direction.addLocal(new Vector3f(direction.y, direction.z, direction.x));
		} else if (ddot > -0.99) {
			Vector3f cross = direction.cross(targetorientation.getNormal());
			direction.addLocal(cross);
		}

		direction.normalizeLocal();
		direction.multLocal((float) distance);

		orientation.getLocation().addLocal(direction);
		if (tool != null) {
			tool.setOrientation(orientation.getLocation(), orientation.getNormal(), orientation.getAngle());
		}
	}

	private void moveToTarget(Vector3f vdistance, double distance, double maxdistance) {
		// just move to right direction
		double scale = 0.8;
		if (distance < maxdistance / 10) {
			// close enough, just go there.
			scale = 1;
		}
		Vector3f scaled = vdistance.mult((float) scale);
		orientation.getLocation().addLocal(scaled);
	}

	private void printOut(double dtime) {
		if (orientationprintouttimer > ORIENTATION_PRINTOUT) {
			debugInfo(dtime);
			orientationprintouttimer = 0;
		}
	}

	private void log(String string) {
		log.info("" + Long.toString(System.currentTimeMillis() - movestarttime) + "ms -- " + string);
	}

	private void debugInfo(double dtime) {
		log("tool:" + tool + " orientation " + orientation + " step:" + dtime + " targetorientation:"
				+ targetorientation);
	}

	@Override
	public boolean isAvailable(CTToolState toolstate) {
		return tool == null || this.tool.equals(toolstate);
	}

	@Override
	public CTEvents getEvents() {
		return this.events;
	}
}
