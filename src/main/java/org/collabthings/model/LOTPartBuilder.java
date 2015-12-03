package org.collabthings.model;

import org.collabthings.environment.impl.LOTRunEnvironmentImpl;

public interface LOTPartBuilder extends LOTObject {

	void setScript(LOTScript s);

	boolean run(LOTPart p);

}
