package org.collabthings.sbmq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import waazdoh.client.utils.WLogger;

public class SBBridge {
	private WLogger logger = WLogger.getLogger(this);
	private boolean closed = false;
	private SBMQConfig config;

	public SBBridge(SBMQConfig config) {
		this.config = config;

		runCmd("status").consume((f, message) -> {
			logger.info("got status message " + message);
			f.close();
		});
	}

	public SBFuture runCmd(String string) {
		String params = "" + string;
		try {
			String sbotpath = checkSBot();

			String path = System.getenv("PATH");
			path += ":" + sbotpath;
			logger.info("env " + path);

			String cmd = "PATH=" + path;
			cmd += " HOME=" + config.getHome();
			cmd += " && sbot " + params;

			logger.info("running " + cmd);
			
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			logger.info("running " + pb.command());
			Process p = pb.start();

			startErrorOutput(p);

			return new SBFuture(p);
		} catch (IOException e) {
			WLogger.getLogger(this).error(e);
			return null;
		}
	}

	private void startErrorOutput(Process p) {
		new Thread(() -> {
			try {
				InputStream is = p.getErrorStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

				while (!closed) {
					String line;
					line = br.readLine();
					if (line == null) {
						break;
					}

					logger.info("error: " + line);
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}).start();
	}

	private String checkSBot() {
		return "/home/juuso/git/collabthings/collabthings.service/ssbc-service/node_modules/.bin";
	}

	public void close() {
		closed = true;
	}

}
