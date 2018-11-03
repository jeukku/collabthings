package org.collabthings.sbmq.message;

public class SBMessageContent {
	private String type;

	private String msg;
	private String text;

	private SBMessageAddress address;

	public SBMessageAddress getAddress() {
		return address;
	}

	public void setAddress(SBMessageAddress address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
