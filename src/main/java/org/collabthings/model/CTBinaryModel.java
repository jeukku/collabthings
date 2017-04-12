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
import java.io.InputStream;

public interface CTBinaryModel extends CTObject, CTModel {
	File getModelFile() throws IOException;

	boolean importModel(String type, InputStream is);

	String getType();

	void setType(String string);

	void setContent(byte[] bytes);

	byte[] getContent();

}
