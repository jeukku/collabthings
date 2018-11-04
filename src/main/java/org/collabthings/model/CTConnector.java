package org.collabthings.model;

import org.collabthings.datamodel.WObject;
import org.collabthings.datamodel.WObjectID;

public interface CTConnector {

	CTApplication getApplication();

	WObjectID getID();

	void save();

	void publish();

	WObject getObject();

}
