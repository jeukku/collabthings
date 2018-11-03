package org.collabthings.sbmq;

public class SBMQConfig {

	private String home = System.getenv("HOME");

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

}
