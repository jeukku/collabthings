package org.collabthings;

import java.util.HashMap;
import java.util.Map;

import waazdoh.client.BinarySource;
import waazdoh.client.ReportingService;
import waazdoh.client.WClient;
import waazdoh.client.model.WBinaryID;
import waazdoh.client.model.objects.WBinary;
import waazdoh.cp2p.messaging.MMessageHandler;

public class StaticBinarySource implements BinarySource {

	private WClient client;
	private Map<WBinaryID, WBinary> bins = new HashMap<>();

	@Override
	public void close() {

	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public void setClient(WClient client) {
		this.client = client;
	}

	@Override
	public WClient getClient() {
		return client;
	}

	@Override
	public WBinary get(WBinaryID streamid) {
		return bins.get(streamid);
	}

	@Override
	public WBinary getOrDownload(WBinaryID binaryid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearMemory(int suggestedmemorytreshold) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReportingService(ReportingService rservice) {
		// TODO Auto-generated method stub

	}

	@Override
	public WBinary newBinary(String comment, String extension) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void waitUntilReady() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startClosing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void published(WBinaryID id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addMessageHandler(String messagename, MMessageHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStats() {
		// TODO Auto-generated method stub
		return null;
	}

}
