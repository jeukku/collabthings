package org.collabthings.util;

import java.awt.Color;
import java.awt.Stroke;

import org.collabthings.math.LVector;

public interface LOTGraphics {

	void setColor(Color black);

	void drawString(String string, int x, int y);

	int getWidth();

	int getHeight();

	void drawRect(int x, int y, int w, int h);

	void drawLine(int asx, int asy, int bsx, int bsy);

	void drawOval(int x, int y, int w, int h);

	void setStroke(Stroke st);

	void drawLine(double x, double y, double z, double x2, double y2, double z2);

	void drawTriangle(LVector ta, LVector tb, LVector tc, int color);

}