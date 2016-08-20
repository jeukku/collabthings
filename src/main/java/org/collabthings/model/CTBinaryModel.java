package org.collabthings.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

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

	Vector3f getTranslation();

	File getModelFile() throws SAXException, IOException;

	void setTranslation(Vector3f Vector3f);

	void setScale(double i);

	boolean importModel(String type, InputStream is);

	boolean importModel(File stl);

	String getType();

	void setType(String string);

	CTTriangleMesh getTriangleMesh();

}
