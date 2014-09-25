package org.libraryofthings.unittests;

import org.libraryofthings.LOTClient;
import org.libraryofthings.LOTTestCase;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.environment.impl.LOTPartState;
import org.libraryofthings.model.LOTFactory;
import org.libraryofthings.model.impl.LOTEnvironmentImpl;

public final class TestFactoryState extends LOTTestCase {

	public void testDestroyPart() {
		LOTClient c = getNewClient();
		LOTEnvironmentImpl e = new LOTEnvironmentImpl(c);
		LOTFactory f = c.getObjectFactory().getFactory();
		LOTFactoryState state = new LOTFactoryState(c, e, "state", f);
		state.newPart();
		LOTPartState p = state.getParts().iterator().next();
		p.destroy();
		//
		assertTrue(state.getParts().size() == 0);
	}
}
