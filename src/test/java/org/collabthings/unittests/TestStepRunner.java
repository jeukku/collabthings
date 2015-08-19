package org.collabthings.unittests;

import org.collabthings.LOTTestCase;
import org.collabthings.simulation.LOTStepRunner;

import waazdoh.client.utils.ConditionWaiter;
import waazdoh.common.MTimedFlag;

public final class TestStepRunner extends LOTTestCase {
	class Values {
		int count = 0;
		double totaltime = 0;

	}

	public void testRun() throws InterruptedException {
		Values v = new Values();

		MTimedFlag f = new MTimedFlag(20000);
		LOTStepRunner runner = new LOTStepRunner(0.00002, 0.00001, (double step) -> {
			v.count++;
			v.totaltime += step;

			if (v.count > 1000) {
				f.trigger();
			}

			return !f.isTriggered();
		});

		f.waitTimer();

		assertTrue("" + v.count, v.count >= 1000);
	}

	public void testStop() throws InterruptedException {
		MTimedFlag f = new MTimedFlag(2000000);
		LOTStepRunner runner = new LOTStepRunner(0.02, 0.01, (double step) -> {
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

	public void testExceptionInTest() throws InterruptedException {
		MTimedFlag f = new MTimedFlag(2000000);
		LOTStepRunner runner = new LOTStepRunner(0.02, 0.01, (double step) -> {
			throw new RuntimeException();
		});
		assertFalse(runner.isStopped());
		assertFalse(f.isTriggered());
		synchronized (runner) {
			runner.wait(2000);
		}
		new ConditionWaiter(() -> runner.isStopped(), 1000);
		//
		assertTrue(runner.isStopped());
	}
}
