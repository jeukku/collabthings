package org.libraryofthings.model;

import java.io.File;
import java.io.IOException;

import org.libraryofthings.math.LVector;
import org.xml.sax.SAXException;

import waazdoh.client.model.Binary;
import waazdoh.util.MStringID;

public interface LOT3DModel extends LOTObject {

	Binary getBinary();

	void setName(String string);

	String getName();

	boolean load(MStringID stringID);

	double getScale();

	LVector getTranslation();

	File getModelFile() throws SAXException, IOException;

}
