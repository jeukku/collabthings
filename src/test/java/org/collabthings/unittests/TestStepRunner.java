package org.collabthings.unittests;

import org.collabthings.CTTestCase;
import org.collabthings.simulation.CTStepRunner;

import waazdoh.client.utils.ConditionWaiter;
import waazdoh.common.WTimedFlag;

public final class TestStepRunner extends CTTestCase {
	class Values {
		int count = 0;
		double totaltime = 0;

	}

	public void testRun() throws InterruptedException {
		Values v = new Values();

		WTimedFlag f = new WTimedFlag(20000);
		CTStepRunner runner = new CTStepRunner(0.00002, 0.00001, (double step) -> {
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
		WTimedFlag f = new WTimedFlag(2000000);
		CTStepRunner runner = new CTStepRunner(0.02, 0.01, (double step) -> {
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
		ConditionWaiter.wait(() -> runner.isStopped(), 20000);
		assertTrue(runner.isStopped());
		assertFalse(f.isTriggered());
	}

	public void testExceptionInTest() throws InterruptedException {
		WTimedFlag f = new WTimedFlag(2000000);
		CTStepRunner runner = new CTStepRunner(0.02, 0.01, (double step) -> {
			throw new RuntimeException();
		});
		assertFalse(runner.isStopped());
		assertFalse(f.isTriggered());
		synchronized (runner) {
			runner.wait(2000);
		}
		ConditionWaiter.wait(() -> runner.isStopped(), 1000);
		//
		assertTrue(runner.isStopped());
	}
}
