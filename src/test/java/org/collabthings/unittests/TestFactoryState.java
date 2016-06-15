package org.collabthings.unittests;

import org.collabthings.CTClient;
import org.collabthings.CTTestCase;
import org.collabthings.environment.CTRunEnvironment;
import org.collabthings.environment.impl.CTFactoryState;
import org.collabthings.environment.impl.CTPartState;
import org.collabthings.environment.impl.CTToolState;
import org.collabthings.math.LVector;
import org.collabthings.model.CTFactory;
import org.collabthings.model.impl.CTEnvironmentImpl;
import org.collabthings.simulation.CTStepRunner;

import waazdoh.common.MTimedFlag;

public final class TestFactoryState extends CTTestCase {

	private CTClient c;

	public void testDestroyPart() {
		CTFactoryState state = getFactoryState();
		state.newPart();
		CTPartState p = state.getParts().iterator().next();
		p.destroy();
		//
		assertTrue(state.getParts().isEmpty());
	}

	public void testGetFactory() {
		CTClient c = getNewClient();
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		CTFactory f = c.getObjectFactory().getFactory();

		f.addFactory();
		f.addFactory();
		f.addFactory("testname");

		CTFactoryState state = new CTFactoryState(c, e, "state", f);

		assertNotNull(state.getFactory("testname"));
		assertNull(state.getFactory("testname_FAIL"));
	}

	public void testGetFactoriesEmpty() {
		CTFactoryState state = getFactoryState();

		assertNull(state.getFactory("testname"));

		assertNotNull(state.getFactories());
		assertTrue(state.getFactories().isEmpty());
	}

	private CTFactoryState getFactoryState() {
		c = getNewClient();
		CTEnvironmentImpl e = new CTEnvironmentImpl(c);
		CTFactory f = c.getObjectFactory().getFactory();

		CTFactoryState state = new CTFactoryState(c, e, "state", f);
		return state;
	}

	public void testTools() {
		CTFactoryState s = getFactoryState();
		CTToolState tool1 = s.addTool("tool", c.getObjectFactory().getTool());
		CTToolState tool2 = s.addTool("tool", c.getObjectFactory().getTool());

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
		CTFactoryState state = getFactoryState();
		CTRunEnvironment rune = state.getRunEnvironment();

		MTimedFlag flag = new MTimedFlag(10000);
		MTimedFlag delay = new MTimedFlag(400);

		new CTStepRunner(0.1, 0.01, (dtime) -> {
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
