package org.libraryofthings.model;

import waazdoh.common.ObjectID;
import waazdoh.common.WData;



public interface LOTObject {

	boolean isReady();

	void publish();

	void save();

	ObjectID getID();

	WData getBean();

}
