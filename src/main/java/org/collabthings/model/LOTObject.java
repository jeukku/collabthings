package org.collabthings.model;

import waazdoh.common.ObjectID;
import waazdoh.common.WObject;

public interface LOTObject {

	boolean isReady();

	void publish();

	void save();

	ObjectID getID();

	WObject getObject();

	boolean parse(WObject o);

}
