package org.collabthings.sbmq.message;

public class SBMessageContainer {
	private SBMessage value;
	private String key;
	private long timestamp;

	public SBMessage getValue() {
		return value;
	}

	public void setValue(SBMessage value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}