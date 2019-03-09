package com.cloud.cc.service;

import com.cloud.cc.vo.TaskDeviceAss;
import com.cloud.cc.vo.TaskWithBLOBs;

public interface TaskService {

	int operTask(TaskWithBLOBs task,String[] deviceId,Integer uitId);
	
	int delTaskDeviceAss(Integer[] deviceId);
	
	TaskDeviceAss selectTaskId(Integer deviceId,Integer userId);
	
	TaskWithBLOBs selectById(Integer Id);
}
