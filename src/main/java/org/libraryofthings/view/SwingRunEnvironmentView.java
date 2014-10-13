package org.libraryofthings.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

public class SwingRunEnvironmentView extends JPanel implements LOTGraphics {

	private RunEnviromentDrawer drawer;
	private Graphics2D g2;

	public SwingRunEnvironmentView(RunEnviromentDrawer ndrawer) {
		this.drawer = ndrawer;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		drawer.draw(this);
		//
		repaint(1000);
	}

	@Override
	public void setStroke(Stroke st) {
		g2.setStroke(st);
	}

	@Override
	public void setColor(Color black) {
		g2.setColor(black);
	}

	@Override
	public void drawString(String string, int i, int j) {
		g2.drawString(string, i, j);
	}

	@Override
	public void drawRect(int ax, int ay, int w, int hy) {
		g2.drawRect(ax, ay, w, hy);
	}

	@Override
	public void drawLine(int asx, int asy, int bsx, int bsy) {
		g2.drawLine(asx, asy, bsx, bsy);
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
		g2.drawOval(x, y, w, h);
	}

}
