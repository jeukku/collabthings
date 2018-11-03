package org.collabthings.sbmq.message;

public class SBMessage {
	private String previous;
	private String type;
	private String key;
	private String author;
	private String signature;
	private long sequence;
	private long timestamp;

	private String hash;

	private SBMessageProgress progress;
	private SBMessageContent content;

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public SBMessageProgress getProgress() {
		return progress;
	}

	public void setProgress(SBMessageProgress progress) {
		this.progress = progress;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public SBMessageContent getContent() {
		return content;
	}

	public void setContent(SBMessageContent content) {
		this.content = content;
	}

	class SBMessageProgress {

	}

	@Override
	public String toString() {
		return "SBMessage type:" + type;
	}
}
