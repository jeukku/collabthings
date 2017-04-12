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

package org.collabthings.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PrintOut {
	public static final int INDENT = 1;
	public static final int INDENT2 = 2;

	private List<Line> lines = new ArrayList<>();

	public void append(String string) {
		append(0, string);
	}

	public void append(int i, String text) {
		StringTokenizer st = new StringTokenizer(text, "\n");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			Line l = new Line();
			l.indent = i;
			l.text = t;
			lines.add(l);
		}
	}

	public String toText() {
		StringBuilder sb = new StringBuilder();
		for (Line l : lines) {
			for (int i = 0; i < l.indent; i++) {
				sb.append("  ");
			}
			sb.append(l.text);
			sb.append("\n");
		}

		return sb.toString();
	}

	public void append(int i, PrintOut printOut) {
		List<Line> olines = printOut.lines;
		for (Line line : olines) {
			int iline = line.indent + i;
			append(iline, line.text);
		}
	}

	private class Line {
		private int indent;
		private String text;
	}

}
