package org.collabthings.model;

import org.collabthings.math.LTransformation;
import org.collabthings.math.LVector;

public interface CTSubPart {

	CTPart getPart();

	LTransformation getTransformation();

	LVector getLocation();

	void setPart(CTPart part);

	LVector getNormal();

	double getAngle();

	void setOrientation(LVector location, LVector normal, double angle);

	void setAngle(double angle);

	void publish();

	void save();

	void set(CTSubPart subpart);

	String getName();

	void setName(String text);

}
