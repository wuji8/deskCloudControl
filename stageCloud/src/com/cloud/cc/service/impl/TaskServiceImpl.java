package com.cloud.cc.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.cc.mapper.TaskDeviceAssMapper;
import com.cloud.cc.mapper.TaskMapper;
import com.cloud.cc.mapper.UiSaveMapper;
import com.cloud.cc.service.TaskService;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.TaskDeviceAss;
import com.cloud.cc.vo.TaskWithBLOBs;
import com.cloud.cc.vo.UiSave;
@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskMapper taskMapper;
	
	@Autowired
	private TaskDeviceAssMapper tdaMapper;
	
	@Autowired
	private UiSaveMapper uiSaveMapper;
	
	
	@Override
	public int operTask(TaskWithBLOBs task, String[] deviceId, Integer uitId) {
		// TODO Auto-generated method stub
		taskMapper.insertSelective(task);
		for (int i = 0; i < deviceId.length; i++) {
			if(StringUnits.isEmpty(deviceId[i]) || !StringUnits.isInteger(deviceId[i])){
				return 3;	//选择正确的设备
			}
			TaskDeviceAss taskDeviceAss=new TaskDeviceAss();
			taskDeviceAss.setCreatetime(new Date());
			taskDeviceAss.setDeviceid(Integer.parseInt(deviceId[i]));
			taskDeviceAss.setDotimes(1);
			taskDeviceAss.setTaskid(task.getTaskid());
			taskDeviceAss.setUserid(task.getUserid());
			tdaMapper.insertSelective(taskDeviceAss);
		}
		UiSave uiSave=new UiSave();
		uiSave.setTaskid(task.getTaskid());
		uiSave.setUisave(task.getUisave());
		uiSave.setUserid(task.getUserid());
		uiSave.setUitid(uitId);
		uiSaveMapper.insertSelective(uiSave);
		return 1;
	}


	@Override
	public int delTaskDeviceAss(Integer[] deviceId) {
		// TODO Auto-generated method stub
		for (int i = 0; i < deviceId.length; i++) {
			tdaMapper.delTaskDeviceAssByDevice(deviceId[i]);
		}
		return 1;
	}


	@Override
	public TaskDeviceAss selectTaskId(Integer deviceId, Integer userId) {
		// TODO Auto-generated method stub
		return tdaMapper.selectByDeviceIdUserId(deviceId, userId);
	}


	@Override
	public TaskWithBLOBs selectById(Integer Id) {
		// TODO Auto-generated method stub
		return taskMapper.selectByPrimaryKey(Id);
	}

}
