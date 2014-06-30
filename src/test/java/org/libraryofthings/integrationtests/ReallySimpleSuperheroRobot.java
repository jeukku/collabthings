package org.libraryofthings.integrationtests;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.RunEnvironment;
import org.libraryofthings.environment.LOTToolState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.math.LVector;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private static final long WAIT_A_BIT = 200;
	private static final float MOVING_LOCATION_LENGTH_TRIGGER = 0.001f;
	private LOTEnvironment env;
	private RunEnvironment simenv;
	private LOTToolState tool;
	private LVector targetnormal;
	private LVector targetlocation;
	private LVector location = new LVector();
	private LVector normal = new LVector(1, 0, 0);
	private boolean stopped;

	public ReallySimpleSuperheroRobot(final LOTEnvironment env,
			RunEnvironment simenv) {
		this.env = env;
		this.simenv = simenv;

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				doRun();
			}
		});
		t.start();
	}

	@Override
	public synchronized void move(LVector l, LVector n) {
		targetlocation = l.copy();
		targetnormal = n.copy();
		//
		while (!isStopped()
				&& location.getSub(targetlocation).length() > MOVING_LOCATION_LENGTH_TRIGGER) {
			waitAWhile();
		}
	}

	private boolean isStopped() {
		return this.stopped;
	}

	@Override
	public void setTool(LOTToolState lotToolState) {
		this.tool = lotToolState;
	}

	private synchronized void doRun() {
		try {
			while (simenv.isRunning()) {
				if (targetlocation != null) {
					LVector direction = targetlocation.getSub(location);
					direction.mult(0.01);
					location.add(direction);
					tool.setLocation(location, targetnormal);
				}
				waitAWhile();
			}
		} finally {
			stopped = true;
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
