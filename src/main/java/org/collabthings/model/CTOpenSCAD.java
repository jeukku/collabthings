package org.collabthings.model;

import waazdoh.common.MStringID;

public interface CTOpenSCAD extends CTObject, CTModel {

	String getError();

	void setScript(String nscript);

	boolean isOK();

	String getName();

	CTModel getModel();

	void setName(String string);

	String getScript();

	boolean load(MStringID id);
}
