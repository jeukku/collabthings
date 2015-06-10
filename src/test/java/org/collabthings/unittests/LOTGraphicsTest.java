package org.collabthings.unittests;

import java.awt.Color;
import java.awt.Stroke;

import org.collabthings.math.LVector;
import org.collabthings.view.LOTGraphics;

public class LOTGraphicsTest implements LOTGraphics {
	public int setcolorcount = 0;
	public int drawstring;
	private int getwidth;
	private int getheight;
	private int drawrect;
	private int drawline;
	private int drawoval;
	private int setstroke;
	private int drawtriangle;

	@Override
	public void setColor(Color black) {
		setcolorcount++;
	}

	@Override
	public void drawString(String string, int x, int y) {
		this.drawstring++;
	}

	@Override
	public int getWidth() {
		this.getwidth++;
		return 100;
	}

	@Override
	public int getHeight() {
		this.getheight++;
		return 100;
	}

	@Override
	public void drawRect(int x, int y, int w, int h) {
		this.drawrect++;
	}

	@Override
	public void drawLine(int asx, int asy, int bsx, int bsy) {
		this.drawline++;
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
		this.drawoval++;
	}

	@Override
	public void setStroke(Stroke st) {
		this.setstroke++;
	}

	@Override
	public void drawLine(double x, double y, double z, double x2, double y2,
			double z2) {
		this.drawline++;
	}

	@Override
	public void drawTriangle(LVector ta, LVector tb, LVector tc, int color) {
		this.drawtriangle++;
	}

}
