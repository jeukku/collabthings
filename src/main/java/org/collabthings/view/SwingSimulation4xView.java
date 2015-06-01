package org.collabthings.view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.vecmath.Vector3d;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.math.LTransformation;

public class SwingSimulation4xView extends JPanel {
	private static final long serialVersionUID = -6344995091052142118L;

	private LTransformation freetransform;
	private double freeangle;
	private SwingRunEnvironmentView yview;
	private SwingRunEnvironmentView zview;
	private SwingRunEnvironmentView xview;
	private SwingRunEnvironmentView fview;
	private double freeanglespeed;

	public SwingSimulation4xView(LOTRunEnvironment runenv) {
		super();
		setupFreeTransform();
		freeanglespeed = runenv.getClient().getPreferences()
				.getDouble("simulation.view.freeangle.speed", 0.2);

		RunEnviromentDrawer ycanvas = new RunEnviromentDrawer(runenv,
				(v, b) -> {
					v.y = v.z;
					v.z = 0;
				}, "Y");
		RunEnviromentDrawer xcanvas = new RunEnviromentDrawer(runenv,
				(v, b) -> {
					v.x = v.z;
					v.z = 0;
				}, "X");
		RunEnviromentDrawer zcanvas = new RunEnviromentDrawer(runenv,
				(v, b) -> v.z = 0, "Z");
		RunEnviromentDrawer freecanvas = new RunEnviromentDrawer(runenv,
				(v, b) -> {
					if (b) {
						freetransform.transform(v);
						v.z += 30;
						v.z /= 10;
						v.x /= v.z;
						v.y /= v.z;
					} else {
						freetransform.transformw0(v);
					}
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
		freeangle += dtime * freeanglespeed;
		setupFreeTransform();
		//
		doRepaint();
	}

	private void setupFreeTransform() {
		LTransformation nfreetransform = new LTransformation();
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(1, 0, 0),
				-0.4));
		nfreetransform.mult(LTransformation.getRotate(new Vector3d(0, 1, 0),
				freeangle));

		freetransform = nfreetransform;
	}
}
