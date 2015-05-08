package org.collabthings.model;

import waazdoh.common.MStringID;

public interface LOTOpenSCAD extends LOTObject {

	String getError();

	void setScript(String nscript);

	boolean isOK();

	String getName();

	LOT3DModel getModel();

	void setName(String string);

	String getScript();

	boolean load(MStringID id);
}
