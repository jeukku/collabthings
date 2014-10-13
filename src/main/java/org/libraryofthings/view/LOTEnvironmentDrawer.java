package org.libraryofthings.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.libraryofthings.math.LTransformationStack;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;

public class LOTEnvironmentDrawer {
	private LOTGraphics g;

	private float[] boundingboxdash = { 5.0f };

	private int screen_right;
	private int screen_left;
	private int screen_top;
	private int screen_bottom;

	private LVector a = new LVector();
	private LVector b = new LVector();

	private EnvironmentDrawerTransform t;
	private double zoom = 100.0;
	private int framecount;
	private String name;
	private boolean somethingoutofscreen;
	private double zoomspeed = 1;

	public LOTEnvironmentDrawer(String nname, EnvironmentDrawerTransform transform) {
		this.name = nname;
		this.t = transform;
	}

	public void init() {
		getGraphics().setColor(Color.black);
		getGraphics().drawString(
				"" + name + " frame:" + (framecount++) + " zoom:" + zoom
						+ " zspeed:" + zoomspeed, 20, 20);

		somethingoutofscreen = false;
		int widthmargin = getGraphics().getWidth() / 10;
		int heightmargin = getGraphics().getHeight() / 10;

		screen_left = widthmargin;
		screen_top = heightmargin;
		screen_right = getGraphics().getWidth() - widthmargin;
		screen_bottom = getGraphics().getHeight() - heightmargin;
	}

	@Override
	public String toString() {
		return "D[" + name + "][" + this.framecount + "]";
	}

	public void checkZoom() {
		if (somethingoutofscreen) {
			zoomspeed *= 0.999;
		} else {
			zoomspeed *= 1.1;
		}

		if (zoomspeed < 0.9) {
			zoomspeed = 0.9;
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

	private void drawLine(LTransformationStack tstack, LVector a, LVector b) {
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
		if (sx > screen_right || sx < screen_left || sy < screen_top
				|| sy > screen_bottom) {
			this.somethingoutofscreen = true;
		}
	}

	private int getSY(LVector l) {
		return (int) ((-zoom * l.y * getGraphics().getHeight() / 1000.0) + getGraphics()
				.getHeight() / 2);
	}

	private int getSX(LVector l) {
		return (int) ((zoom * l.x * getGraphics().getHeight() / 1000.0) + getGraphics()
				.getWidth() / 2);
	}

	public void drawString(LTransformationStack tstack, String string, LVector l) {
		tstack.current().transform(a);
		t.transform(a);
		g.drawString(string, getSX(a), getSY(a));
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

		getGraphics().drawRect(sx - 2, sy - 2, 4, 4);
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
