package org.collabthings.model;

public interface CTHeightmap extends CTModel, CTObject {

	String getScript();

	void setScript(String sscripttext);

	boolean isOK();

	String getError();

}
