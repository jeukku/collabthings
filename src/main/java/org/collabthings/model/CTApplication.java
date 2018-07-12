package org.collabthings.model;

import java.util.List;

import org.collabthings.model.impl.CTApplicationImpl.ApplicationLine;

public interface CTApplication extends CTObject {

	void setApplication(String stext);

	String getInfo();

	List<ApplicationLine> getLines();

	boolean isOK();

	void addLine(ApplicationLine setline);

}
