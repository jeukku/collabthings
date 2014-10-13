package org.libraryofthings.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.vecmath.Vector3d;

import org.libraryofthings.LLog;
import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.math.LTransformation;
import org.libraryofthings.math.LVector;

public class SwingSimulationView {

	private static final double MAX_TEXTUPDATETIME = 2;
	LOTRunEnvironment runenv;
	private RunEnviromentDrawer ycanvas;
	private RunEnviromentDrawer xcanvas;
	private RunEnviromentDrawer zcanvas;
	private RunEnviromentDrawer freecanvas;
	private double freeangle;

	LLog log = LLog.getLogger(this);
	private JFrame f;
	private LTransformation freetransform;

	public SwingSimulationView(LOTRunEnvironment runenv) {
		this.runenv = runenv;
		SwingUtilities.invokeLater(() -> createFrame());
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createFrame() {
		if (runenv.isRunning()) {
			f = new JFrame();
			f.setSize(800, 500);
			ycanvas = new RunEnviromentDrawer(runenv, (v) -> {
				v.y = v.z;
				v.z = 0;
			}, "Y");
			xcanvas = new RunEnviromentDrawer(runenv, (v) -> {
				v.x = v.z;
				v.z = 0;
			}, "X");
			zcanvas = new RunEnviromentDrawer(runenv, (v) -> {
				v.z = 0;
			}, "Z");
			freecanvas = new RunEnviromentDrawer(runenv, (v) -> {
				freetransform.transform(v);
			}, "Z");

			f.getContentPane().setLayout(new BorderLayout(0, 0));

			JPanel panel = new JPanel();
			f.getContentPane().add(panel);
			panel.setLayout(new GridLayout(2, 2));

			panel.add(new SwingRunEnvironmentView(ycanvas));
			panel.add(new SwingRunEnvironmentView(xcanvas));
			panel.add(new SwingRunEnvironmentView(zcanvas));
			panel.add(new SwingRunEnvironmentView(freecanvas));

			f.setAutoRequestFocus(true);
			// f.setExtendedState(JFrame.MAXIMIZED_BOTH);
			f.setVisible(true);
			f.toFront();
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	public void step(double dtime) {
		freeangle += dtime * 0.00002;
		LTransformation nfreetransform = new LTransformation();
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(1, 0, 0),
				0.4));
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(0, 1, 0),
				freeangle));
		freetransform = nfreetransform;
	}

	interface TransformV {
		void transform(LVector v);
	}

	public void close() {
		if (f != null) {
			f.dispose();
		}
	}
}
