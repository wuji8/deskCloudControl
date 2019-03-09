package com.cloud.cc.service;

import java.util.List;

import com.cloud.cc.tools.PageHelper;
import com.cloud.cc.vo.DevicePool;
import com.cloud.cc.vo.Devices;
import com.cloud.cc.vo.Users;

public interface DevicesService {

	
	int addDevice(Devices devices);
	
	
	int delDevice(Integer[] devicesId,Integer userId);
	
	
	void queryPage(PageHelper<Devices> pageHelper);
	
	int updateDevice(Devices devices);
	
	Devices selectById(Integer deviceId);
	
	Devices selectByUdId(String udId,Integer userId);
	
	int addDevicePool(DevicePool devicePool);
	
	List<DevicePool> selectByUserId(Integer userId);
	
	int operDevicePoolDevice(String[] dpIds,Users user);
	
	int delDevicePool(String[] dpIds,Users user);
}
