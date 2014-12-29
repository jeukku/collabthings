package org.libraryofthings.model;

import waazdoh.client.model.WData;
import waazdoh.client.model.ObjectID;

public interface LOTObject {

	boolean isReady();

	void publish();

	void save();

	ObjectID getID();

	WData getBean();

}
