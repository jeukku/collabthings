package org.collabthings.simulation;

import org.collabthings.util.LLog;

public class CTStepRunner {

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
		double lasttime = System.currentTimeMillis();
		try {
			while (!stopped) {
				double now = System.currentTimeMillis();
				double dt = (now - lasttime) / 1000.0;
				if (dt > minstep) {
					lasttime = now;

					if (dt > maxstep) {
						dt = maxstep;
					}

					if (totalcount % 10000 == 0) {
						printInfo();
					}
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

	private void printInfo() {
		if (totalcount > 0) {
			log.info("Stepper count:" + totalcount + " time:" + totaltime + " avg.step:" + (totaltime / totalcount));
		}
	}

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
