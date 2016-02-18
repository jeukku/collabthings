package org.collabthings.model;

import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;

public interface LOTSubPart {

	LOTPart getPart();

	LTransformation getTransformation();

	LVector getLocation();

	void setPart(LOTPart part);

	LVector getNormal();

	double getAngle();

	void setOrientation(LVector location, LVector normal, double angle);

}
