package org.collabthings.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.collabthings.math.LVector;
import org.xml.sax.SAXException;

import waazdoh.client.model.objects.Binary;
import waazdoh.common.MStringID;

public interface LOT3DModel extends LOTObject {

	Binary getBinary();

	void setName(String string);

	String getName();

	boolean load(MStringID stringID);

	double getScale();

	LVector getTranslation();

	File getModelFile() throws SAXException, IOException;

	void setTranslation(LVector lVector);

	void setScale(double i);

	boolean importModel(InputStream is);

}
