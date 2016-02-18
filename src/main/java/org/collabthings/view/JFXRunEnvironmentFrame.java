package org.collabthings.view;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.view.JFXSimulationView.ViewCanvas;

public class JFXRunEnvironmentFrame {
	private LOTRunEnvironment env;
	private JFrame f;
	private JFXSimulationView view;

	public JFXRunEnvironmentFrame(LOTRunEnvironment env) {
		this.env = env;
		createFrame();
	}

	public void createFrame() {
		SwingUtilities.invokeLater(() -> doCreateFrame());
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private synchronized void doCreateFrame() {
		f = new JFrame();
		f.setSize(1200, 800);
		f.getContentPane().setLayout(new BorderLayout());

		JFXPanel panel = new JFXPanel();
		view = new JFXSimulationView(env);
		view.setCanvas(new ViewCanvas() {
			@Override
			public boolean isVisible() {
				return panel.isShowing() && panel.isVisible();
			}

			@Override
			public void setScene(Scene scene) {
				panel.setScene(scene);
			}

			@Override
			public Point2D getUpperLeft() {
				Point d = panel.getLocationOnScreen();
				return new Point2D(d.x, d.y);
			}

			@Override
			public double getWidth() {
				return panel.getWidth();
			}

			@Override
			public double getHeight() {
				return panel.getHeight();
			}

			@Override
			public void refresh() {
				// TODO
			}
		});

		panel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				view.mouseMove(e.getX(), e.getY(), e.getButton());
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				view.mouseMove(e.getX(), e.getY(), e.getButton());
			}
		});

		panel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				view.mouseUp(e.getX(), e.getY(), e.getButton());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				view.mouseDown(e.getX(), e.getY(), e.getButton());
			}

		});

		f.getContentPane().add(panel, BorderLayout.CENTER);

		f.setVisible(true);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
	}

	public void close() {
		if (f != null) {
			f.dispose();
		}
	}

	public void step(double dtime) {
		if (view != null) {
			view.step(dtime);
		}
	}

}
