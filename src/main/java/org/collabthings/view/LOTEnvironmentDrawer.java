package org.collabthings.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.collabthings.math.LTransformationStack;
import org.collabthings.math.LVector;
import org.collabthings.model.LOTBoundingBox;

public class LOTEnvironmentDrawer {
	private static final int INFOTEXT_Y = 20;
	private static final int INFOTEXT_X = 5;
	private static final double DEFAULT_ZOOM = 100.0;
	private static final double DEFAULT_ZOOMSPEED = 1;

	private LOTGraphics g;

	private float[] boundingboxdash = { 5.0f };

	private int screenright;
	private int screenleft;
	private int screentop;
	private int screenbottom;

	private LVector a = new LVector();
	private LVector b = new LVector();

	protected EnvironmentDrawerTransform t;
	private double zoom = DEFAULT_ZOOM;
	private int framecount;
	private String name;
	private boolean somethingoutofscreen;
	private double zoomspeed = DEFAULT_ZOOMSPEED;

	public LOTEnvironmentDrawer(String nname,
			EnvironmentDrawerTransform transform) {
		this.name = nname;
		this.t = transform;
	}

	public void init() {
		getGraphics().setColor(Color.black);
		getGraphics().drawString(
				"" + name + " frame:" + (framecount++) + " zoom:" + zoom
						+ " zspeed:" + zoomspeed, INFOTEXT_X, INFOTEXT_Y);

		somethingoutofscreen = false;
		int widthmargin = getGraphics().getWidth() / 10;
		int heightmargin = getGraphics().getHeight() / 10;

		screenleft = widthmargin;
		screentop = heightmargin;
		screenright = getGraphics().getWidth() - widthmargin;
		screenbottom = getGraphics().getHeight() - heightmargin;
	}

	@Override
	public String toString() {
		return "D[" + name + "][" + this.framecount + "]";
	}

	public void checkZoom(double dtime) {
		if (somethingoutofscreen) {
			zoomspeed *= (1 - dtime / 10);
		} else {
			zoomspeed *= (1 + dtime);
		}

		if (zoomspeed < 0.4) {
			zoomspeed = 0.4;
		} else if (zoomspeed > 1.0) {
			zoomspeed = 1.0;
		}

		zoom += (zoomspeed - 1);
		if (zoom < 0.01) {
			zoom = 0.01;
		} else if (zoom > 100) {
			zoom = 100;
		}
	}

	public void setGraphics(LOTGraphics g2) {
		this.g = g2;
	}

	public LOTGraphics getGraphics() {
		return g;
	}

	protected void drawLine(LTransformationStack tstack, LVector a, LVector b) {
		tstack.current().transform(a);
		tstack.current().transform(b);

		t.transform(a);
		t.transform(b);
		int asx = getSX(a);
		int asy = getSY(a);
		int bsx = getSX(b);
		int bsy = getSY(b);

		getGraphics().drawLine(asx, asy, bsx, bsy);
	}

	public void drawCenterCircle(LTransformationStack tstack, LVector l) {
		a.set(l);
		tstack.current().transform(a);
		t.transform(a);
		int sx = getSX(a);
		int sy = getSY(a);
		checkOutOfScreen(sx, sy);
		getGraphics().setColor(Color.blue);
		getGraphics().drawOval(sx - 5, sy - 5, 10, 10);
	}

	private void checkOutOfScreen(int sx, int sy) {
		if (sx > screenright || sx < screenleft || sy < screentop
				|| sy > screenbottom) {
			this.somethingoutofscreen = true;
		}
	}

	public int getSY(LVector l) {
		double y = l.y;
		return getSY(y);
	}

	public int getSY(double y) {
		return (int) ((-zoom * y * getGraphics().getHeight() / 1000.0) + getGraphics()
				.getHeight() / 2);
	}

	public int getSX(LVector l) {
		double x = l.x;
		return getSX(x);
	}

	public int getSX(double x) {
		return (int) ((zoom * x * getGraphics().getHeight() / 1000.0) + getGraphics()
				.getWidth() / 2);
	}

	public void drawString(LTransformationStack tstack, String string, LVector l) {
		a.set(l);
		tstack.current().transform(a);
		t.transform(a);
		getGraphics().drawString(string, getSX(a), getSY(a));
	}

	public void drawCenterSquare(LTransformationStack tstack, LVector l) {
		Stroke st = new BasicStroke(2);
		getGraphics().setStroke(st);

		a.set(l);
		tstack.current().transform(a);
		t.transform(a);
		int sx = getSX(a);
		int sy = getSY(a);
		checkOutOfScreen(sx, sy);

		int w = 4;
		int h = 4;

		getGraphics().drawRect(sx - w / 2, sy - h / 2, w, h);
	}

	public void drawScreenRect(LTransformationStack tstack, LVector l, int w,
			int h) {
		Stroke st = new BasicStroke(2);
		getGraphics().setStroke(st);

		a.set(l);
		tstack.current().transform(a);
		t.transform(a);
		int sx = getSX(a);
		int sy = getSY(a);
		checkOutOfScreen(sx, sy);

		getGraphics().drawRect(sx, sy, w, h);
	}

	public void drawBoundingBox(LTransformationStack tstack, LOTBoundingBox bbox) {
		a.set(bbox.getA());
		b.set(bbox.getB());

		Stroke st = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, boundingboxdash, 0.0f);
		getGraphics().setStroke(st);

		getGraphics().setColor(Color.gray);

		drawLine(tstack, new LVector(a.x, a.y, a.z), new LVector(a.x, b.y, a.z));
		drawLine(tstack, new LVector(a.x, b.y, a.z), new LVector(a.x, b.y, b.z));
		drawLine(tstack, new LVector(a.x, b.y, b.z), new LVector(a.x, a.y, b.z));
		drawLine(tstack, new LVector(a.x, a.y, b.z), new LVector(a.x, a.y, a.z));

		drawLine(tstack, new LVector(b.x, a.y, a.z), new LVector(b.x, b.y, a.z));
		drawLine(tstack, new LVector(b.x, b.y, a.z), new LVector(b.x, b.y, b.z));
		drawLine(tstack, new LVector(b.x, b.y, b.z), new LVector(b.x, a.y, b.z));
		drawLine(tstack, new LVector(b.x, a.y, b.z), new LVector(b.x, a.y, a.z));

		drawLine(tstack, new LVector(a.x, a.y, a.z), new LVector(b.x, a.y, a.z));
		drawLine(tstack, new LVector(a.x, b.y, a.z), new LVector(b.x, b.y, a.z));
		drawLine(tstack, new LVector(a.x, b.y, b.z), new LVector(b.x, b.y, b.z));
		drawLine(tstack, new LVector(a.x, a.y, b.z), new LVector(b.x, a.y, b.z));
	}

}
