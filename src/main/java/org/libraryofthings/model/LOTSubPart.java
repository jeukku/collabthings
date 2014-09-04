package org.libraryofthings.model;

import org.libraryofthings.math.LVector;

public interface LOTSubPart {

	LOTPart getPart();

	LVector getLocation();

	void setPart(LOTPart part);

	LVector getNormal();

	void setOrientation(LVector location, LVector normal);

}
