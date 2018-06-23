package org.collabthings.model;

import java.util.List;

import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;

public interface CTApplication extends CTObject {

	void setScript(String stext);

	String getInfo();

	List<ApplicationLine> getContent();

	boolean isOK();

	void addApplicationLine(ApplicationLine setline);

}
