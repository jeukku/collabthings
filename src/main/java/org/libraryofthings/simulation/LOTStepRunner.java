package org.libraryofthings.simulation;

import org.libraryofthings.LLog;

public class LOTStepRunner {

	private Thread thread;
	private double maxstep;
	private LLog log = LLog.getLogger(this);
	private boolean stopped;

	public LOTStepRunner(final double maxstep, final StepListener listener) {
		this.maxstep = maxstep;
		this.thread = new Thread(() -> launchLoop(listener));
		this.thread.start();
	}

	private void launchLoop(final StepListener listener) {
		long st = System.currentTimeMillis();
		try {
			runLoop(listener);
		} finally {
			log.info("Exiting in " + (System.currentTimeMillis() - st) + "ms");
			thread = null;
		}
	}

	private void runLoop(final StepListener listener) {
		double lasttime = System.currentTimeMillis();
		while (!stopped) {
			double dt = System.currentTimeMillis() - lasttime;
			if (dt > maxstep) {
				dt = maxstep;
			}

			boolean isstillrunning = listener.step(dt);
			if (!isstillrunning) {
				break;
			}
		}
	}

	public interface StepListener {
		boolean step(double dtime);
	}

	public void stop() {
		stopped = true;
	}

}
