package org.libraryofthings.view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.vecmath.Vector3d;

import org.libraryofthings.environment.LOTRunEnvironment;
import org.libraryofthings.math.LTransformation;

public class SwingSimulation4xView extends JPanel {
	private LTransformation freetransform;
	private double freeangle;

	public SwingSimulation4xView(LOTRunEnvironment runenv) {
		super();

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

		add(new SwingRunEnvironmentView(ycanvas));
		add(new SwingRunEnvironmentView(xcanvas));
		add(new SwingRunEnvironmentView(zcanvas));
		add(new SwingRunEnvironmentView(freecanvas));
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
}
