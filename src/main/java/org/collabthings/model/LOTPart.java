package org.collabthings.model;

import java.io.File;
import java.util.List;

import org.collabthings.math.LVector;

public interface LOTPart extends LOTObject {

	LOTSubPart newSubPart();

	List<LOTSubPart> getSubParts();

	void setName(String string);

	LOTModel getModel();

	boolean isAnEqualPart(LOTPart b);

	String getName();

	boolean importModel(File file);

	void setBoundingBox(LVector a, LVector b);

	LOTBoundingBox getBoundingBox();

	LOTOpenSCAD newSCAD();

	LOTBinaryModel newBinaryModel();

	LOTMaterial getMaterial();

}
