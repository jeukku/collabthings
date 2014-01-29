package org.libraryofthings;

import org.libraryofthings.model.LOTPart;

import waazdoh.cutils.MID;

public interface RunEnvironment {

	void setParameter(String string, MID id);

	void addPart(String string, LOTPart destinationpart);

}
