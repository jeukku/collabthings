package org.libraryofthings.model;

import waazdoh.client.model.JBean;
import waazdoh.client.model.MID;

public interface LOTObject {

	boolean isReady();

	void publish();

	void save();

	MID getID();

	JBean getBean();

}
