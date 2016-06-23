package org.collabthings.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.collabthings.math.LVector;
import org.xml.sax.SAXException;

import waazdoh.client.model.objects.Binary;
import waazdoh.common.MStringID;

public interface CTBinaryModel extends CTObject, CTModel {
	public static final String TYPE_X3D = "x3d";
	public static final String TYPE_STL = "stl";
	public static final String TYPE = "binary";

	Binary getBinary();

	void setName(String string);

	String getName();

	boolean load(MStringID stringID);

	double getScale();

	LVector getTranslation();

	File getModelFile() throws SAXException, IOException;

	void setTranslation(LVector lVector);

	void setScale(double i);

	boolean importModel(String type, InputStream is);

	boolean importModel(File stl);

	String getType();

	CTTriangleMesh getTriangleMesh();

}
