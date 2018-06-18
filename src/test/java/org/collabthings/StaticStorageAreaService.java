package org.collabthings;

import java.util.LinkedList;
import java.util.List;

import waazdoh.common.service.StorageAreaService;
import waazdoh.datamodel.ReturnVO;
import waazdoh.datamodel.StorageAreaSearchVO;
import waazdoh.datamodel.StorageAreaVO;

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
