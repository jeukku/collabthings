package org.collabthings;

import java.util.LinkedList;
import java.util.List;

import collabthings.common.service.StorageAreaService;
import collabthings.datamodel.ReturnVO;
import collabthings.datamodel.StorageAreaSearchVO;
import collabthings.datamodel.StorageAreaVO;

public class StaticStorageAreaService implements StorageAreaService {

	@Override
	public List<String> listNewItems(String userid, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> list(StorageAreaVO vo) {
		return new LinkedList<>();
	}

	@Override
	public ReturnVO write(StorageAreaVO item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StorageAreaVO read(StorageAreaVO vo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> searchValue(StorageAreaSearchVO vo) {
		return new LinkedList<>();
	}

}
