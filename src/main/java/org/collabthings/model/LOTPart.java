package org.collabthings.model;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.collabthings.math.LVector;

public interface LOTPart extends LOTObject {

	LOTSubPart newSubPart();

	List<LOTSubPart> getSubParts();

	void setName(String string);

	LOT3DModel getModel();

	boolean isAnEqualPart(LOTPart b);

	void newModel();

	String getName();

	boolean importModel(File file);

	boolean importModel(InputStream resourceAsStream);

	void setBoundingBox(LVector a, LVector b);

	LOTBoundingBox getBoundingBox();

}
