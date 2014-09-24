package org.libraryofthings.model;

import java.io.File;
import java.util.List;

import org.libraryofthings.math.LVector;

public interface LOTPart extends LOTObject {

	LOTSubPart newSubPart();

	List<LOTSubPart> getSubParts();

	void setName(String string);

	LOT3DModel getModel();

	boolean isAnEqualPart(LOTPart b);

	void newModel();

	String getName();

	boolean importModel(File file);

	void setBoundingBox(LVector lVector, LVector lVector2);

	LOTBoundingBox getBoundingBox();

}
