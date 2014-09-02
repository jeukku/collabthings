package org.libraryofthings.environment;

import org.libraryofthings.math.LVector;

public interface LOTRuntimeObject {

	LVector getLocation();

	LVector getAbsoluteLocation();

	void setParentFactory(LOTFactoryState nfactorystate);

	void step(double dtime);

	void stop();

}
