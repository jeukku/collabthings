package org.libraryofthings.model;

import waazdoh.client.model.ObjectID;
import waazdoh.client.model.WData;

public interface LOTObject {

	boolean isReady();

	void publish();

	void save();

	ObjectID getID();

	WData getBean();

}
