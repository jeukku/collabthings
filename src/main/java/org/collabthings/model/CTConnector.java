package org.collabthings.model;

import waazdoh.datamodel.WObject;
import waazdoh.datamodel.WObjectID;

public interface CTConnector {

	CTApplication getApplication();

	WObjectID getID();

	void save();

	void publish();

	WObject getObject();

}
