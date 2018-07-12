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
package org.collabthings.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.collabthings.CTListener;
import org.collabthings.model.impl.CTResourceUsage;

import com.jme3.math.Vector3f;

public interface CTPart extends CTObject {

	CTSubPart newSubPart();

	List<CTSubPart> getSubParts();

	CTModel getModel();

	boolean isAnEqualPart(CTPart b);

	void setName(String string);

	String getName();

	void setShortname(String sname);

	String getShortname();

	boolean importModel(File file) throws IOException;

	void resetModel();

	void setBoundingBox(Vector3f a, Vector3f b);

	CTBoundingBox getBoundingBox();

	CTOpenSCAD newSCAD();

	CTBinaryModel newBinaryModel();

	CTMaterial getMaterial();

	void removeSubPart(CTSubPart subpart);

	CTPartBuilder getBuilder();

	CTPartBuilder newBuilder();

	void addChangeListener(CTListener listener);

	CTHeightmap newHeightmap();

	CTHeightmap getHeightmap();

	CTResourceUsage getResourceUsage();

	void updateResourceUsage();

	boolean hasChanged();

	CTViewingProperties getViewingProperties();

	CTVectorGroup getVectorGroup(String string);

	CTVectorGroup addVectorGroup(String string);

	CTConnector addSubpartConnector(String string);

}
