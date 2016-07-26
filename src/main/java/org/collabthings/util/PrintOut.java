package org.collabthings.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PrintOut {
	public static final int INDENT = 1;
	public static final int INDENT2 = 2;

	private List<Line> lines = new ArrayList<Line>();

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
		int indent;
		String text;
	}

}
