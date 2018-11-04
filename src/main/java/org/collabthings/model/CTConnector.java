package org.collabthings.model;

import collabthings.datamodel.WObject;
import collabthings.datamodel.WObjectID;

public interface CTConnector {

	CTApplication getApplication();

	WObjectID getID();

	void save();

	void publish();

	WObject getObject();

}
