package org.collabthings.model.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.collabthings.math.LVector;
import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;
import org.xml.sax.SAXException;

public class CTTriangleMeshImpl implements CTTriangleMesh {

	private List<CTTriangle> ts = new ArrayList<CTTriangle>();
	private List<LVector> vs = new ArrayList<LVector>();

	@Override
	public List<CTTriangle> getTriangles() {
		return ts;
	}

	@Override
	public List<LVector> getVectors() {
		return vs;
	}

}
