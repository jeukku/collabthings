package org.libraryofthings.environment;

import org.libraryofthings.math.LVector;

public interface LOTToolUser {

	void move(LVector l, LVector n);

	void setTool(LOTToolState lotToolState);

	void step(double dtime);
}
