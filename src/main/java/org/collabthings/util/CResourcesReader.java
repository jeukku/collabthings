package org.collabthings.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

public class CResourcesReader {

	private boolean success;
	private String text;

	public CResourcesReader(String string) {
		read(string);
	}

	private void read(String string) {
		try (InputStream s = ClassLoader.getSystemResourceAsStream(string)) {
			if (s != null) {
				StringWriter w = new StringWriter();
				IOUtils.copy(s, w);
				this.text = w.getBuffer().toString();
				this.success = true;
			} else {
				LLog.getLogger(this).info("Resource " + string + " not found");
			}
		} catch (IOException e) {
			LLog.getLogger(this).error(this, "read", e);
		}
	}

	public boolean isSuccess() {
		return success;
	}

	public String getText() {
		return this.text;
	}

}
