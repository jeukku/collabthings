package org.collabthings.unittests;

import org.collabthings.LOTClient;
import org.collabthings.LOTTestCase;
import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.environment.impl.LOTFactoryState;
import org.collabthings.environment.impl.LOTPartState;
import org.collabthings.environment.impl.LOTToolState;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTFactory;
import org.collabthings.model.impl.LOTEnvironmentImpl;
import org.collabthings.simulation.LOTStepRunner;

import waazdoh.common.MTimedFlag;

public final class TestFactoryState extends LOTTestCase {

	private LOTClient c;

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
		c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		LOTFactory f = c.getObjectFactory().getFactory();

		LOTFactoryState state = new LOTFactoryState(c, e, "state", f);
		return state;
	}

	public void testTools() {
		LOTFactoryState s = getFactoryState();
		LOTToolState tool1 = s.addTool("tool", c.getObjectFactory().getTool());
		LOTToolState tool2 = s.addTool("tool", c.getObjectFactory().getTool());

		tool1.setInUse();
		tool1.setOrientation(new LVector(1000, 0, 0), new LVector(0, 1, 0), 0);
		tool2.setOrientation(new LVector(), new LVector(0, 1, 0), 0);

		assertEquals(tool2, s.getTool("tool"));
		tool1.setAvailable();
		tool2.setInUse();

		assertEquals(tool1, s.getTool("tool"));

		assertNull(s.getTool("tool_UNKNOWN"));
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
