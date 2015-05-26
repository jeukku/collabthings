package org.collabthings.model;

import waazdoh.common.MStringID;

public interface LOTOpenSCAD extends LOTObject {

	String TYPE = "openscad";

	String getError();

	void setScript(String nscript);

	boolean isOK();

	String getName();

	LOTBinaryModel getModel();

	void setName(String string);

	String getScript();

	boolean load(MStringID id);
}
