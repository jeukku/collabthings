package org.libraryofthings.simulation;

public class LOTStepRunner {

	private Thread thread;
	private double maxstep;

	public LOTStepRunner(final double maxstep, final StepListener listener) {
		this.maxstep = maxstep;
		this.thread = new Thread(() -> {
			try {
				runLoop(listener);
			} finally {
				thread = null;
			}
		});
		this.thread.start();
	}

	private void runLoop(final StepListener listener) {
		double lasttime = System.currentTimeMillis();
		while (true) {
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

}
