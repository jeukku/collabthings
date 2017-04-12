/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.simulation;

import org.collabthings.util.LLog;

public final class CTStepRunner {

	private Thread thread;
	private double maxstep;
	private double minstep;

	private LLog log = LLog.getLogger(this);
	private boolean stopped;
	private double totaltime;
	private int totalcount;

	public CTStepRunner(final double maxstep, final double minstep, final StepListener listener) {
		this.maxstep = maxstep;
		this.minstep = minstep;
		this.thread = new Thread(() -> launchLoop(listener), "StepRunner launchLoop");
		this.thread.start();
	}

	private void launchLoop(final StepListener listener) {
		long st = System.currentTimeMillis();
		try {
			synchronized (this) {
				this.notifyAll();
			}
			runLoop(listener);
		} finally {
			log.info("Exiting in " + (System.currentTimeMillis() - st) + "ms");
			thread = null;
			stop();
		}
	}

	private synchronized void runLoop(final StepListener listener) {
		try {
			tryLoop(listener);
		} catch (InterruptedException e) {
			log.error(this, "step", e);
			Thread.currentThread().interrupt();
		}

		thread = null;
		stopped = true;
		//
		synchronized (this) {
			this.notifyAll();
		}

		printInfo();
	}

	private synchronized void tryLoop(final StepListener listener) throws InterruptedException {
		double lasttime = System.currentTimeMillis();
		while (!stopped) {
			double now = System.currentTimeMillis();
			double dt = (now - lasttime) / 1000.0;
			if (dt > minstep) {
				lasttime = now;

				dt = maxStep(dt);

				testAndprintOut();

				totaltime += dt;
				totalcount++;

				boolean isstillrunning = listener.step(dt);
				if (!isstillrunning) {
					break;
				}
			} else {
				double ddt = minstep - dt;
				int timeout = (int) (ddt * 1000);
				if (timeout <= 0) {
					timeout = (int) (minstep / 2 * 1000);
					timeout++;
				}
				wait(timeout);
			}
		}
	}

	private void testAndprintOut() {
		if (totalcount % 10_000 == 0) {
			printInfo();
		}
	}

	private double maxStep(double dt) {
		if (dt > maxstep) {
			return maxstep;
		} else {
			return dt;
		}
	}

	private void printInfo() {
		if (totalcount > 0) {
			log.info("Stepper count:" + totalcount + " time:" + totaltime + " avg.step:" + (totaltime / totalcount));
		}
	}

	@FunctionalInterface
	public interface StepListener {
		boolean step(double dtime);
	}

	public void stop() {
		stopped = true;
	}

	public boolean isStopped() {
		return stopped && thread == null;
	}

}
