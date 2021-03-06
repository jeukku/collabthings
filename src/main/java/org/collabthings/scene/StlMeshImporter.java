/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/

package org.collabthings.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

import org.collabthings.model.CTTriangle;
import org.collabthings.model.CTTriangleMesh;
import org.collabthings.model.impl.CTTriangleMeshImpl;
import org.collabthings.util.LLog;

import com.jme3.math.Vector3f;

public class StlMeshImporter {

	private LLog log = LLog.getLogger(this);
	private String content;

	public void setFile(File file) {
		this.content = getFileContent(file);
	}

	public void setContent(String ncontent) {
		this.content = ncontent;
	}

	public CTTriangleMesh getImport() {
		String c = this.content;
		if (c != null) {
			Vector3f n = new Vector3f(0, 1, 0);
			CTTriangleMeshImpl mesh = new CTTriangleMeshImpl();

			StringTokenizer st = new StringTokenizer(c);
			Deque<Vector3f> s = new ArrayDeque<>();
			while (st.hasMoreTokens()) {
				String t = st.nextToken();
				if ("vertex".equals(t)) {
					float sx = Float.parseFloat(st.nextToken());
					float sy = Float.parseFloat(st.nextToken());
					float sz = Float.parseFloat(st.nextToken());
					s.push(new Vector3f(sx, sy, sz));
				} else if ("normal".equals(t)) {
					float sx = Float.parseFloat(st.nextToken());
					float sy = Float.parseFloat(st.nextToken());
					float sz = Float.parseFloat(st.nextToken());
					n = new Vector3f(sx, sy, sz);
				} else if ("endfacet".equals(t)) {
					int index = mesh.getVectors().size();
					mesh.add(s.pop());
					mesh.add(s.pop());
					mesh.add(s.pop());
					mesh.add(new CTTriangle(index, index + 1, index + 2, n));
				}
			}

			return mesh;
		} else {
			return null;
		}
	}

	private String getFileContent(File file) {
		try (BufferedReader r = new BufferedReader(new FileReader(file))) {
			StringBuilder b = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				b.append(line);
				b.append("\n");
			}
			r.close();
			return b.toString();
		} catch (IOException e) {
			log.error(this, "getModelFileContent", e);
			return null;
		}
	}

}
