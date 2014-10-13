package org.libraryofthings.view;

import java.awt.Color;
import java.awt.Stroke;

public interface LOTGraphics {

	void setColor(Color black);

	void drawString(String string, int i, int j);

	int getWidth();

	int getHeight();

	void drawRect(int i, int j, int k, int l);

	void drawLine(int asx, int asy, int bsx, int bsy);

	void drawOval(int i, int j, int k, int l);

	void setStroke(Stroke st);

}
