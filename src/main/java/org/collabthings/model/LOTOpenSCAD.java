package org.collabthings.model;

public interface LOTOpenSCAD extends LOTObject {

	String getError();

	void setScript(String nscript);

	boolean isOK();

	String getName();

}
