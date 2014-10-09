package org.libraryofthings.model;

import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;

public interface LOTSubPart {

	LOTPart getPart();

	LTransformation getTransformation();

	LVector getLocation();

	void setPart(LOTPart part);

	LVector getNormal();

	double getAngle();

	void setOrientation(LVector location, LVector normal, double angle);

	void setOrientation(LVector location, LVector normal);

}
