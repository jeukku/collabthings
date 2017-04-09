package org.collabthings.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.jme3.math.Vector3f;

import waazdoh.client.model.objects.WBinary;
import waazdoh.common.WStringID;

public interface CTBinaryModel extends CTObject, CTModel {
	String VALUE_TYPE_X3D = "x3d";
	String VALUE_TYPE_STL = "stl";
	String VALUE_TYPE = "binary";

	// WBinary getBinary();

	void setName(String string);

	@Override
	String getName();

	@Override
	boolean load(WStringID stringID);

	@Override
	double getScale();

	@Override
	Vector3f getTranslation();

	File getModelFile() throws IOException;

	@Override
	void setTranslation(Vector3f Vector3f);

	@Override
	void setScale(double i);

	boolean importModel(String type, InputStream is);

	@Override
	boolean importModel(File stl) throws IOException;

	String getType();

	void setType(String string);

	@Override
	CTTriangleMesh getTriangleMesh();

	void setContent(byte[] bytes);

	byte[] getContent();

}
