package org.libraryofthings.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.libraryofthings.LLog;
import org.libraryofthings.LOTClient;

public class LOTJavaScriptLoader implements ScriptLoader {
	private String path;
	private LLog log = LLog.getLogger(this);
	//
	private Set<String> forbiddenwords = new HashSet<>();

	private static LOTJavaScriptLoader loader;

	public static ScriptLoader get(LOTClient env, String string) {
		if (loader == null) {
			loader = new LOTJavaScriptLoader(env, string);
		}
		return loader;
	}

	private LOTJavaScriptLoader(LOTClient c, String npath) {
		this.path = npath;
		String words = c.getGlobalSetting(LOTClient.JAVASCRIPT_FORBIDDENWORDS);
		StringTokenizer st = new StringTokenizer(words);
		while (st.hasMoreTokens()) {
			String word = st.nextToken();
			forbiddenwords.add(word);
		}
		//
		forbiddenwords.add("import");
		forbiddenwords.add("java");
		forbiddenwords.add("io");
		forbiddenwords.add("new");
	}

	@Override
	public Invocable load(String s) throws ScriptException {
		checkScript(s);
		//
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

	private void checkScript(String s) {
		StringTokenizer st = new StringTokenizer(s,
				"\n\r\t ;:(){}[]!@#$%^&_+-.");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (forbiddenwords.contains(t)) {
				throw new SecurityException("not accepting " + t);
			}
		}
	}

	public static void reset() {
		loader = null;
	}
}
