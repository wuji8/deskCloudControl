package com.cloud.cc.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.cc.mapper.DevicePoolMapper;
import com.cloud.cc.mapper.DevicesMapper;
import com.cloud.cc.mapper.LogsMapper;
import com.cloud.cc.service.DevicesService;
import com.cloud.cc.tools.PageHelper;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.DevicePool;
import com.cloud.cc.vo.Devices;
import com.cloud.cc.vo.Logs;
import com.cloud.cc.vo.Users;
@Service
public class DevicesServiceImpl implements DevicesService{

	@Autowired
	private DevicesMapper devicesMapper;
	
	@Autowired
	private LogsMapper logsMapper;
	
	@Autowired
	private DevicePoolMapper devicePoolMapper;
	
	@Override
	public int addDevice(Devices devices) {
		// TODO Auto-generated method stub
		int result=devicesMapper.insertSelective(devices);
		if(result>0) {
			Logs logs=new Logs();
			logs.setContent("添加了设备【"+devices.getRemark()+"】");
			logs.setCreatetime(new Date());
			logs.setUserid(devices.getUserid());
			logs.setType(3);
			logsMapper.insertSelective(logs);
		}
		return result;
	}

	@Override
	public int delDevice(Integer[] devicesId,Integer userId) {
		// TODO Auto-generated method stub
		int result=0;
		String content="";
		for (int i = 0; i < devicesId.length; i++) {
			result+=devicesMapper.deleteByPrimaryKey(devicesId[i]);
			Devices devices=devicesMapper.selectByPrimaryKey(devicesId[i]);
			content+=devices.getRemark()+",";
		}
		content=content.substring(0,content.length()-1);
		Logs logs=new Logs();
		logs.setContent("删除了设备【"+content+"】");
		logs.setCreatetime(new Date());
		logs.setUserid(userId);
		logs.setType(5);
		logsMapper.insertSelective(logs);
		return result;
	}

	@Override
	public void queryPage(PageHelper<Devices> pageHelper) {
		// TODO Auto-generated method stub
		pageHelper.setRows(devicesMapper.queryPage(pageHelper.getParams()));
		pageHelper.setTotal(devicesMapper.countPage(pageHelper.getParams()));
	}

	@Override
	public int updateDevice(Devices devices) {
		// TODO Auto-generated method stub
		return devicesMapper.updateByPrimaryKeySelective(devices);
	}

	@Override
	public Devices selectById(Integer deviceId) {
		// TODO Auto-generated method stub
		return devicesMapper.selectByPrimaryKey(deviceId);
	}

	@Override
	public Devices selectByUdId(String udId,Integer userId) {
		// TODO Auto-generated method stub
		return devicesMapper.selectByUdId(udId,userId);
	}

	@Override
	public int addDevicePool(DevicePool devicePool) {
		// TODO Auto-generated method stub
		return devicePoolMapper.insertSelective(devicePool);
	}

	@Override
	public List<DevicePool> selectByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return devicePoolMapper.selectByUserId(userId);
	}

	/**
	 * 授权设备
	 */
	@Override
	public int operDevicePoolDevice(String[] dpIds, Users user) {
		// TODO Auto-generated method stub
		for (int i = 0; i < dpIds.length; i++) {
			if(StringUnits.isEmpty(dpIds[i]) || !StringUnits.isInteger(dpIds[i])){
				return 3;	//请填写至少一个设备
			}
			//判断是否已存在设备列表中
			DevicePool devicePool=devicePoolMapper.selectByPrimaryKey(Integer.parseInt(dpIds[i]));
			if(devicePool!=null){
				if(selectByUdId(devicePool.getUdid(), user.getUserid())==null){
					Devices devices=new Devices();
					devices.setCreatetime(new Date());
					devices.setUdid(devicePool.getUdid());
					devices.setUserid(user.getUserid());
					devicesMapper.insertSelective(devices);
					devicePoolMapper.deleteByPrimaryKey(devicePool.getDpid());
				}
			}
		}
		return 1;
	}

	/**
	 * 拒绝授权设备
	 */
	@Override
	public int delDevicePool(String[] dpIds, Users user) {
		for (int i = 0; i < dpIds.length; i++) {
			if(StringUnits.isEmpty(dpIds[i]) || !StringUnits.isInteger(dpIds[i])){
				return 3;	//请填写至少一个设备
			}
			//判断是否已存在设备列表中
			DevicePool devicePool=devicePoolMapper.selectByPrimaryKey(Integer.parseInt(dpIds[i]));
			if(devicePool!=null){
				if(selectByUdId(devicePool.getUdid(), user.getUserid())==null){
					devicePoolMapper.deleteByPrimaryKey(devicePool.getDpid());
				}
			}
		}
		return 1;
	}
}
