package org.collabthings.model;

import waazdoh.common.WStringID;
import waazdoh.common.WObjectID;
import waazdoh.common.WObject;

public interface CTObject {

	boolean isReady();

	void publish();

	void save();

	WObjectID getID();

	WObject getObject();

	boolean parse(WObject o);

	String getName();

	void setName(String n);

	boolean load(WStringID id);

}
