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

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

public interface CTSubPart {

	CTPart getPart();

	Transform getTransformation();

	Vector3f getLocation();

	void setPart(CTPart part);

	Vector3f getNormal();

	double getAngle();

	void setOrientation(Vector3f location, Vector3f normal, double angle);

	void setAngle(double angle);

	void publish();

	void save();

	void set(CTSubPart subpart);

	String getName();

	void setName(String text);

	String getNamePath();

	void setPartBookmark(String string);

	void updateBookmark();

	String getPartBookmark();

	boolean isBookmarkUpdated();

	boolean hasPartChanged();

}
