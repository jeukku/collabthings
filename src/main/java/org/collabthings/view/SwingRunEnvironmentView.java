package org.collabthings.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JPanel;

import org.collabthings.math.LVector;
import org.collabthings.util.LOTGraphics;

public class SwingRunEnvironmentView extends JPanel implements LOTGraphics {
	private static final long serialVersionUID = -6174408330205773946L;

	private transient RunEnvironmentDrawerImpl drawer;
	private transient Graphics2D g2;

	public SwingRunEnvironmentView(RunEnvironmentDrawerImpl ndrawer) {
		this.drawer = ndrawer;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		drawer.draw(this);
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
	public void drawLine(double x, double y, double z, double bx, double by,
			double bz) {
		g2.drawLine((int) x, (int) y, (int) bx, (int) by);
	}

	@Override
	public void drawTriangle(LVector ta, LVector tb, LVector tc, int color) {
		setColor(color);

		int[] ys = new int[3];
		int[] xs = new int[3];
		xs[0] = (int) ta.x;
		ys[0] = (int) ta.y;
		xs[1] = (int) tb.x;
		ys[1] = (int) tb.y;
		xs[2] = (int) tc.x;
		ys[2] = (int) tc.y;

		synchronized (g2) {
			g2.fillPolygon(xs, ys, 3);
		}
	}

	private void setColor(int color) {
		Color c = getColor(color);
		g2.setColor(c);
	}

	private Color getColor(int color) {
		return new Color(color);
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
		g2.drawOval(x, y, w, h);
	}

	public void callRepaint() {
		repaint(1000);
	}

}
