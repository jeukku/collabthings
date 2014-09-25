package org.libraryofthings.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.environment.impl.LOTFactoryState;
import org.libraryofthings.environment.impl.LOTPartState;
import org.libraryofthings.environment.impl.LOTToolUser;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTBoundingBox;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTSubPart;

public class SimpleSimulationView {

	private static final double MAX_TEXTUPDATETIME = 20000;
	private LOTRunEnvironment runenv;
	private VCanvas ycanvas;
	private VCanvas xcanvas;
	private VCanvas zcanvas;
	private LLog log = LLog.getLogger(this);
	private JTextArea infotext;
	private double textupdatetime;

	public SimpleSimulationView(LOTRunEnvironment runenv) {
		this.runenv = runenv;
		SwingUtilities.invokeLater(() -> createFrame());
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createFrame() {
		JFrame f = new JFrame();
		f.setSize(800, 800);
		ycanvas = new VCanvas((v) -> {
			return new LVector(v.getX(), v.getZ(), 0);
		}, "Y");
		xcanvas = new VCanvas((v) -> {
			return new LVector(v.getZ(), v.getY(), 0);
		}, "X");
		zcanvas = new VCanvas((v) -> {
			return new LVector(v.getX(), v.getY(), 0);
		}, "Z");
		f.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		f.getContentPane().add(panel);
		panel.setLayout(new GridLayout(2, 2));

		JPanel info = new JPanel();
		info.setLayout(new BorderLayout());
		infotext = new JTextArea();
		info.add(infotext);

		panel.add(ycanvas);
		panel.add(xcanvas);
		panel.add(zcanvas);
		panel.add(info);

		f.setAutoRequestFocus(true);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);
		f.toFront();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void step(double dtime) {
		if (infotext != null && textupdatetime > MAX_TEXTUPDATETIME) {
			textupdatetime = 0.0;
			StringBuilder sb = new StringBuilder();
			sb.append("step:" + dtime + " ");
			sb.append("env:" + this.runenv);
			infotext.setText(sb.toString());
		} else {
			textupdatetime += dtime;
		}
	}

	private class VCanvas extends JPanel {
		private TransformV t;
		private double zoom = 100.0;
		private int framecount;
		private String name;
		private boolean somethingoutofscreen;
		private int screen_right;
		private int screen_left;
		private int screen_top;
		private int screen_bottom;
		private double zoomspeed = 1;

		public VCanvas(TransformV transform, String name) {
			super();
			this.name = name;
			t = transform;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.black);
			g.drawString("" + name + " frame:" + (framecount++) + " zoom:"
					+ zoom + " zspeed:" + zoomspeed, 20, 20);

			somethingoutofscreen = false;
			int widthmargin = getSize().width / 10;
			int heightmargin = getSize().height / 10;

			screen_left = widthmargin;
			screen_top = heightmargin;
			screen_right = getSize().width - widthmargin;
			screen_bottom = getSize().height - heightmargin;

			Set<LOTRuntimeObject> os = runenv.getRunObjects();
			drawObjects(g, os);

			checkZoom();

			repaint();
		}

		private void checkZoom() {
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

		private void drawObjects(Graphics g, Set<LOTRuntimeObject> os) {
			for (LOTRuntimeObject o : os) {
				drawObject(g, o);
			}
		}

		private void drawObject(Graphics g, LOTRuntimeObject o) {
			if (o instanceof LOTFactoryState) {
				drawFactoryState(g, (LOTFactoryState) o);
			} else {
				log.info("unknown object " + o);
			}
		}

		private void drawFactoryState(Graphics g, LOTFactoryState o) {
			LOTBoundingBox bbox = o.getFactory().getBoundingBox();
			if (bbox != null) {
				drawBoundingBox(g, o.getLocation(), bbox);
			}
			// child factories
			List<LOTFactoryState> fs = o.getFactories();
			for (LOTFactoryState lotFactoryState : fs) {
				drawFactoryState(g, lotFactoryState);
			}
			//
			List<LOTToolUser> tus = o.getToolUsers();
			for (LOTToolUser lotToolUser : tus) {
				drawToolUser(g, lotToolUser);
			}

			Set<LOTPartState> parts = o.getParts();
			for (LOTPartState partstate : parts) {
				drawPartState(g, partstate);
			}
		}

		private void drawPartState(Graphics g, LOTPartState partstate) {
			LOTPart part = partstate.getPart();
			if (part != null) {
				drawPart(g, partstate, part);
			}
		}

		private void drawPart(Graphics g, LOTPartState partstate, LOTPart part) {
			LVector l = partstate.getLocation();
			LOTBoundingBox bbox = part.getBoundingBox();
			if (bbox != null) {
				drawBoundingBox(g, l, bbox);
			}

			g.setColor(Color.lightGray);
			g.drawString("" + part, getSX(l), getSY(l) - 10);
			//
			g.setColor(Color.red);
			List<LOTSubPart> subparts = part.getSubParts();
			for (LOTSubPart lotSubPart : subparts) {
				LVector subpartlocation = lotSubPart.getLocation().getAdd(l);
				drawCenterSquare(g, subpartlocation);
			}
		}

		private void drawCenterSquare(Graphics g, LVector subpartlocation) {
			LVector a = t.transform(subpartlocation);
			int sx = getSX(a);
			int sy = getSY(a);
			checkOutOfScreen(sx, sy);
			g.drawRect(sx - 2, sy - 2, 4, 4);
		}

		private void drawBoundingBox(Graphics g, LVector l, LOTBoundingBox bbox) {
			LVector a = bbox.getA().getAdd(l);
			LVector b = bbox.getB().getAdd(l);
			a = t.transform(a);
			b = t.transform(b);
			int asx = getSX(a);
			int asy = getSY(a);
			int bsx = getSX(b);
			int bsy = getSY(b);
			int w = bsx - asx;
			int h = bsy - asy;

			if (w < 0) {
				asx = bsx;
				w = -w;
			}

			if (h < 0) {
				asy = bsy;
				h = -h;
			}

			g.setColor(Color.gray);
			g.drawRect(asx, asy, w, h);
		}

		private void drawToolUser(Graphics g, LOTToolUser tooluser) {
			LVector l = tooluser.getLocation().copy();
			drawCenterCircle(g, l);
		}

		private void drawCenterCircle(Graphics g, LVector l) {
			l = t.transform(l);
			int sx = getSX(l);
			int sy = getSY(l);
			checkOutOfScreen(sx, sy);
			g.setColor(Color.blue);
			g.drawOval(sx - 5, sy - 5, 10, 10);
		}

		private void checkOutOfScreen(int sx, int sy) {
			if (sx > screen_right || sx < screen_left || sy < screen_top
					|| sy > screen_bottom) {
				this.somethingoutofscreen = true;
			}
		}

		private int getSY(LVector l) {
			return (int) ((-zoom * l.getY()) + getHeight() / 2);
		}

		private int getSX(LVector l) {
			return (int) ((zoom * l.getX()) + getWidth() / 2);
		}
	}

	private interface TransformV {
		LVector transform(LVector v);
	}
}
