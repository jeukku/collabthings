package org.collabthings.model;

import java.io.File;
import java.util.List;

import org.collabthings.math.LVector;

public interface CTPart extends CTObject {

	CTSubPart newSubPart();

	List<CTSubPart> getSubParts();

	CTModel getModel();

	boolean isAnEqualPart(CTPart b);

	void setName(String string);

	String getName();

	void setShortname(String sname);

	String getShortname();

	boolean importModel(File file);

	void resetModel();

	void setBoundingBox(LVector a, LVector b);

	CTBoundingBox getBoundingBox();

	CTOpenSCAD newSCAD();

	CTBinaryModel newBinaryModel();

	CTMaterial getMaterial();

	void removeSubPart(CTSubPart subpart);

}
