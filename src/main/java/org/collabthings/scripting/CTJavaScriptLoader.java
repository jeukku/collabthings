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

package org.collabthings.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.collabthings.CTClient;
import org.collabthings.model.impl.CTConstants;
import org.collabthings.util.LLog;

public class CTJavaScriptLoader implements ScriptLoader {
	private static CTJavaScriptLoader loader;

	private LLog log = LLog.getLogger(this);
	//
	private Set<String> forbiddenwords = new HashSet<>();
	private Set<String> libraries = new HashSet<>();

	public static synchronized ScriptLoader get(CTClient env) {
		if (loader == null) {
			CTJavaScriptLoader nloader = new CTJavaScriptLoader();
			if (nloader.init(env)) {
				loader = nloader;
			}
		}
		return loader;
	}

	private boolean init(CTClient c) {
		try {
			String words = c.getGlobalSetting(CTConstants.JAVASCRIPT_FORBIDDENWORDS);
			if (words != null) {
				StringTokenizer st = new StringTokenizer(words);
				while (st.hasMoreTokens()) {
					String word = st.nextToken();
					forbiddenwords.add(word);
				}
			}

			//
			forbiddenwords.add("import");
			forbiddenwords.add("java");
			forbiddenwords.add("io");
			forbiddenwords.add("new");
			//
			initLibraries();
			return true;
		} catch (IOException e) {
			log.error(this, "init", e);
			return false;
		}
	}

	private void initLibraries() throws IOException {
		addLibrary("js/lib.js");
		addLibrary("js/underscore-min.js");
	}

	private void addLibrary(String name) throws IOException {
		try (InputStream is = ClassLoader.getSystemResourceAsStream(name)) {
			BufferedReader r = new BufferedReader(new InputStreamReader(is, CTConstants.CHARSET));

			StringBuilder sb = new StringBuilder();

			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
				sb.append("\n");
			}

			libraries.add(sb.toString());
		}
	}

	@Override
	public Invocable load(String s) throws ScriptException {
		if (s != null) {
			checkScript(s);
			//
			ScriptEngine e = new ScriptEngineManager().getEngineByName("JavaScript");

			for (String script : libraries) {
				e.eval(script);
			}

			e.eval(s);
			Invocable inv = (Invocable) e;

			return inv;
		} else {
			log.info("Null script given");
			return null;
		}
	}

	private void checkScript(String s) {
		StringTokenizer st = new StringTokenizer(s, "\n\r\t ;:(){}[]!@#$%^&_+-.");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (forbiddenwords.contains(t)) {
				throw new SecurityException("not accepting " + t);
			}
		}
	}

	public static synchronized void reset() {
		loader = null;
	}
}
