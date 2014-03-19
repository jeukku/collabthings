package org.libraryofthings.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.libraryofthings.LLog;

public class JavaScriptLoader implements ScriptLoader {
	private String path;

	public JavaScriptLoader(String npath) {
		this.path = npath;
	}

	@Override
	public Invocable load(String s) throws ScriptException {
		ScriptEngine e = new ScriptEngineManager()
				.getEngineByName("JavaScript");

		try {
			e.eval(new FileReader(path + "lib" + File.separatorChar + "js"
					+ File.separatorChar + "underscore-min.js"));
			e.eval(new FileReader(path + "lib" + File.separatorChar + "js"
					+ File.separatorChar + "lib.js"));
			e.eval(s);
			Invocable inv = (Invocable) e;

			return inv;
		} catch (FileNotFoundException e1) {
			LLog.getLogger(this).error(this, "load", e1);
			return null;
		}
	}
}
