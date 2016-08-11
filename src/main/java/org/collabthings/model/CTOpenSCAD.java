package org.collabthings.model;

public interface CTOpenSCAD extends CTObject, CTModel {

	String getError();

	void setScript(String nscript);

	boolean isOK();

	String getName();

	CTModel getModel();

	void setName(String string);

	String getScript();
}
