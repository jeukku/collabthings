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
import org.libraryofthings.environment.LOTFactoryState;
import org.libraryofthings.environment.LOTPartState;
import org.libraryofthings.environment.LOTToolUser;
import org.libraryofthings.environment.RunEnvironment;
import org.libraryofthings.math.LVector;
import org.libraryofthings.model.LOTPart;
import org.libraryofthings.model.LOTRuntimeObject;
import org.libraryofthings.model.LOTSubPart;
import org.libraryofthings.model.impl.LOTBoundingBox;

public class SimpleSimulationView {

	private static final double MAX_TEXTUPDATETIME = 20000;
	private RunEnvironment runenv;
	private VCanvas ycanvas;
	private VCanvas xcanvas;
	private VCanvas zcanvas;
	private LLog log = LLog.getLogger(this);
	private JTextArea infotext;
	private double textupdatetime;

	public SimpleSimulationView(RunEnvironment runenv) {
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

		f.setVisible(true);
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
		private double zoom = 3.0;
		private int framecount;
		private String name;

		public VCanvas(TransformV transform, String name) {
			super();
			this.name = name;
			t = transform;
			setBackground(Color.getHSBColor((float) Math.random(), 1, 1));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.black);
			g.drawString("" + name + " frame:" + (framecount++), 20, 20);
			Set<LOTRuntimeObject> os = runenv.getRunObjects();
			drawObjects(g, os);
			repaint();
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
			drawPart(g, partstate, part);
		}

		private void drawPart(Graphics g, LOTPartState partstate, LOTPart part) {
			LVector l = partstate.getLocation();
			LOTBoundingBox bbox = part.getBoundingBox();
			if (bbox != null) {
				drawBoundingBox(g, l, bbox);
			} else {
				g.drawOval(getSX(l) - 2, getSY(l) - 2, 4, 4);
			}
			//
			List<LOTSubPart> subparts = part.getSubParts();
			for (LOTSubPart lotSubPart : subparts) {
				LVector subpartlocation = lotSubPart.getLocation().getAdd(l);
				LVector a = t.transform(subpartlocation);
				g.drawRect(getSX(a), getSY(subpartlocation), 4, 4);
			}
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

			g.drawRect(asx, asy, w, h);
		}

		private void drawToolUser(Graphics g, LOTToolUser tooluser) {
			LVector l = tooluser.getLocation().copy();
			l = t.transform(l);
			int sx = getSX(l);
			int sy = getSY(l);
			g.drawOval(sx - 5, sy - 5, 10, 10);
		}

		private int getSY(LVector l) {
			return (int) ((zoom * l.getY()) + getHeight() / 2);
		}

		private int getSX(LVector l) {
			return (int) ((zoom * l.getX()) + getWidth() / 2);
		}
	}

	private interface TransformV {
		LVector transform(LVector v);
	}
}
