package org.collabthings;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import waazdoh.client.BinarySource;
import waazdoh.client.ReportingService;
import waazdoh.client.WClient;
import waazdoh.client.model.objects.WBinary;
import waazdoh.datamodel.WStringID;

public class StaticBinarySource implements BinarySource {

	private WClient client;
	private Map<WStringID, WBinary> bins = new HashMap<>();

	@Override
	public void close() {

	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public byte[] readData(String ipfsid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String writeData(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WBinary get(WStringID streamid) {
		return bins.get(streamid);
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
	public void published(WStringID id) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getStats() {
		// TODO Auto-generated method stub
		return null;
	}
}
