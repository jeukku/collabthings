package org.libraryofthings.unittests;

import org.libraryofthings.LOTTestCase;
import org.libraryofthings.simulation.LOTStepRunner;

import waazdoh.util.ConditionWaiter;
import waazdoh.util.MTimedFlag;

public final class TestStepRunner extends LOTTestCase {

	public void testStop() throws InterruptedException {
		MTimedFlag f = new MTimedFlag(2000000);
		LOTStepRunner runner = new LOTStepRunner(0.01, (double step) -> {
			return !f.isTriggered();
		});
		assertFalse(runner.isStopped());
		assertFalse(f.isTriggered());
		synchronized (runner) {
			runner.wait(2000);
		}
		//
		assertFalse(runner.isStopped());
		assertFalse(f.isTriggered());
		runner.stop();
		new ConditionWaiter(() -> runner.isStopped(), 20000);
		assertTrue(runner.isStopped());
		assertFalse(f.isTriggered());
	}
}
