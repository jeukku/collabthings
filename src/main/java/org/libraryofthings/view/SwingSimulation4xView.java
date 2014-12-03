package org.libraryofthings.view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.vecmath.Vector3d;

import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.math.LTransformation;

public class SwingSimulation4xView extends JPanel {
	private LTransformation freetransform;
	private double freeangle;
	private SwingRunEnvironmentView yview;
	private SwingRunEnvironmentView zview;
	private SwingRunEnvironmentView xview;
	private SwingRunEnvironmentView fview;

	public SwingSimulation4xView(LOTRunEnvironment runenv) {
		super();
		setupFreeTransform();

		RunEnviromentDrawer ycanvas = new RunEnviromentDrawer(runenv, (v) -> {
			v.y = v.z;
			v.z = 0;
		}, "Y");
		RunEnviromentDrawer xcanvas = new RunEnviromentDrawer(runenv, (v) -> {
			v.x = v.z;
			v.z = 0;
		}, "X");
		RunEnviromentDrawer zcanvas = new RunEnviromentDrawer(runenv, (v) -> {
			v.z = 0;
		}, "Z");
		RunEnviromentDrawer freecanvas = new RunEnviromentDrawer(runenv,
				(v) -> {
					freetransform.transform(v);
				}, "Z");

		setLayout(new GridLayout(2, 2));

		yview = new SwingRunEnvironmentView(ycanvas);
		add(yview);
		xview = new SwingRunEnvironmentView(xcanvas);
		add(xview);
		zview = new SwingRunEnvironmentView(zcanvas);
		add(zview);
		fview = new SwingRunEnvironmentView(freecanvas);
		add(fview);

	}

	public void doRepaint() {
		yview.callRepaint();
		xview.callRepaint();
		zview.callRepaint();
		fview.callRepaint();
	}

	public void step(double dtime) {
		freeangle += dtime * 0.00002;
		setupFreeTransform();
		//
		doRepaint();
	}

	private void setupFreeTransform() {
		LTransformation nfreetransform = new LTransformation();
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(1, 0, 0),
				0.4));
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(0, 1, 0),
				freeangle));
		freetransform = nfreetransform;
	}
}
