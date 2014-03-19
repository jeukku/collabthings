package org.libraryofthings.build;

import org.libraryofthings.LOTEnvironment;
import org.libraryofthings.environment.LOTToolUser;

public class ReallySimpleSuperheroRobot implements LOTToolUser {

	private LOTEnvironment env;

	public ReallySimpleSuperheroRobot(final LOTEnvironment env) {
		this.env = env;
	}

}
