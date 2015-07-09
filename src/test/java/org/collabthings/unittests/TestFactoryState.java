package org.collabthings.unittests;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.environment.impl.LOTPartState;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.simulation.LOTStepRunner;

import waazdoh.common.MTimedFlag;

public final class TestFactoryState extends LOTTestCase {

	public void testDestroyPart() {
		LOTFactoryState state = getFactoryState();
		state.newPart();
		LOTPartState p = state.getParts().iterator().next();
		p.destroy();
		//
		assertTrue(state.getParts().isEmpty());
	}

	public void testGetFactory() {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		LOTFactory f = c.getObjectFactory().getFactory();

		f.addFactory();
		f.addFactory();
		f.addFactory("testname");

		LOTFactoryState state = new LOTFactoryState(c, e, "state", f);

		assertNotNull(state.getFactory("testname"));
		assertNull(state.getFactory("testname_FAIL"));
	}

	public void testGetFactoriesEmpty() {
		LOTFactoryState state = getFactoryState();

		assertNull(state.getFactory("testname"));

		assertNotNull(state.getFactories());
		assertTrue(state.getFactories().isEmpty());
	}

	private LOTFactoryState getFactoryState() {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		LOTFactory f = c.getObjectFactory().getFactory();

		LOTFactoryState state = new LOTFactoryState(c, e, "state", f);
		return state;
	}

	public void testRuntime() {
		LOTFactoryState state = getFactoryState();
		LOTRunEnvironment rune = state.getRunEnvironment();

		MTimedFlag flag = new MTimedFlag(10000);
		MTimedFlag delay = new MTimedFlag(400);

		LOTStepRunner runner = new LOTStepRunner(0.1, 0.01, (dtime) -> {
			rune.step(dtime);
			return !flag.isTriggered();
		});

		state.stepWhile((dtime) -> {
			if (delay.isTriggered()) {
				flag.trigger();
				return true;
			} else {
				return false;
			}
		});

		assertTrue(flag.wasTriggerCalled());
	}

}
