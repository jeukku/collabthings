package org.collabthings.view;

import java.awt.BorderLayout;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.collabthings.environment.LOTRunEnvironment;
import org.collabthings.view.JFXSimulationView.ViewCanvas;

public class JFXRunEnvironmentFrame {
	private LOTRunEnvironment env;
	private JFrame f;

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
		new JFXSimulationView(env, new ViewCanvas() {

			@Override
			public void setScene(Scene scene) {
				panel.setScene(scene);
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
		env.step(dtime);
	}

}
