package com.cloud.cc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.cc.service.CloudProjectsService;
import com.cloud.cc.service.DevicesService;
import com.cloud.cc.service.TaskService;
import com.cloud.cc.service.UiTableService;
import com.cloud.cc.service.UsersService;
import com.cloud.cc.tools.JsonUtil;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.DevicePool;
import com.cloud.cc.vo.Devices;
import com.cloud.cc.vo.TaskDeviceAss;
import com.cloud.cc.vo.TaskWithBLOBs;
import com.cloud.cc.vo.Users;

@Controller
public class TaskController {

	@Autowired
	private UiTableService uiTableService;
	
	@Autowired
	private CloudProjectsService cloudProjectService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private DevicesService devicesService;
	
	@Autowired
	private UsersService userService;
	
	/**
	 * 根据用户获取项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/getCloudProjectByUser")
	@ResponseBody
	public Map<String,Object> getCloudProjectByUser(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		Users user=(Users)request.getSession().getAttribute("user");
		resultMap.put("data",cloudProjectService.selectByUserId(user.getCloudid()));
		return resultMap;
	}
	
	
	/**
	 * 根据用户获取UI表
	 * @param request
	 * @return
	 */
	@RequestMapping("/getUiTableByUser")
	@ResponseBody
	public Map<String,Object> getUiTableByUser(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String cloudId=request.getParameter("cloudId");
		if(StringUnits.isEmpty(cloudId) || !StringUnits.isInteger(cloudId)){
			resultMap.put("code", 2);	//参数错误
			return resultMap;
		}
		resultMap.put("data", uiTableService.selectByCloudId(Integer.parseInt(cloudId)));
		return resultMap;
	}
	
	/**
	 * 添加任务 
	 * @param request cloudId-项目Id uitId-ui表Id projectName-项目名称 devices-数组选择的设备Id uiJson-ui的Json
	 * @return
	 */
	@RequestMapping(value="addTask")
	@ResponseBody
	public Map<String,Object> addTask(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String cloudId=request.getParameter("cloudId");
		String uitId=request.getParameter("uitId");
		String devices=request.getParameter("devices");
		String uiJson=request.getParameter("uiJson");
		if(StringUnits.isEmpty(cloudId) || !StringUnits.isInteger(cloudId)){
			resultMap.put("code", 2);
			resultMap.put("msg", "请选择正确的脚本");
			return resultMap;
		}
		if(StringUnits.isEmpty(uitId) || !StringUnits.isInteger(uitId)){
			resultMap.put("code", 2);
			resultMap.put("msg", "请选择正确的ui表");
			return resultMap;
		}
		if(StringUnits.isEmpty(uiJson)){
			resultMap.put("code", 2);
			resultMap.put("msg", "请填写正确的ui内容");
			return resultMap;
		}
		if(StringUnits.isEmpty(devices)){
			resultMap.put("code", 2);
			resultMap.put("msg", "请填写正确的设备，至少选择一个设备");
			return resultMap;
		}
		String[] devicesId=devices.substring(0,devices.length()-1).split(",");
		if(devicesId!=null && devicesId.length<0){
			resultMap.put("code", 2);
			resultMap.put("msg", "请选择正确的设备，至少选择一个设备");
			return resultMap;
		}
		Users user=(Users)request.getSession().getAttribute("user");
		TaskWithBLOBs task=new TaskWithBLOBs();
		task.setCloudid(Integer.parseInt(cloudId));
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("userName", user.getUsername());
		map.put("projectName", cloudProjectService.selectById(Integer.parseInt(cloudId)).getCloudname());
		map.put("ui",uiJson);
		task.setContent(JsonUtil.mapToJson(map));
		task.setCreatetime(new Date());
		task.setUisave(uiJson);
		task.setUserid(user.getUserid());
		resultMap.put("code", taskService.operTask(task, devicesId, Integer.parseInt(uitId)));
		return resultMap;
	}
	
	/**
	 * 获取任务API接口
	 * @param request
	 * @return
	 */
	@RequestMapping("/getTask")
	@ResponseBody
	public Map<String,Object> getTask(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String cuid=request.getParameter("cuid");	//云控项目唯一标识
		String udid=request.getParameter("udid");	//设备唯一标识
		String ccid=request.getParameter("ccid");	//云控用户唯一标识
		if(StringUnits.isEmpty(cuid)){
			resultMap.put("code", 0);
			resultMap.put("data", "缺少云控项目唯一标识参数");
			return resultMap;
		}
		if(StringUnits.isEmpty(udid)){
			resultMap.put("code", 0);
			resultMap.put("data", "缺少设备唯一标识参数");
			return resultMap;
		}
		if(StringUnits.isEmpty(ccid)){
			resultMap.put("code", 0);
			resultMap.put("data", "缺少云控用户唯一标识参数");
			return resultMap;
		}
		//获取用户
		Users user=userService.selectUserByCCID(ccid);
		if(user==null){
			resultMap.put("code", 0);
			resultMap.put("data", "用户唯一标识错误，找不到该用户");
			return resultMap;
		}
		//获取设备，如果设备列表没有，则提示用户不存在该设备
		Devices device=devicesService.selectByUdId(udid,user.getUserid());
		if(device==null){
			DevicePool devicePool=new DevicePool();
			devicePool.setCreatetime(new Date());
			devicePool.setUdid(udid);
			devicePool.setUserid(user.getUserid());
			devicesService.addDevicePool(devicePool);
			resultMap.put("code", 0);
			resultMap.put("data", "找不到该设备，请先将该设备添加授权");
			return resultMap;
		}
		//查出任务跟设备的关系对象，如果没有则提示用户
		TaskDeviceAss taskDeviceAss=taskService.selectTaskId(device.getDeviceid(), user.getUserid());
		if(taskDeviceAss==null){
			resultMap.put("code", 0);
			resultMap.put("data","该设备跟任务没有关联");
			return resultMap;
		}
		TaskWithBLOBs task=taskService.selectById(taskDeviceAss.getTaskid());
		resultMap.put("code", 1);
		resultMap.put("data", task.getContent());
		return resultMap;
	}
	
}
