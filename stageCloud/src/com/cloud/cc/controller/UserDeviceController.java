package com.cloud.cc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.cc.service.DevicesService;
import com.cloud.cc.service.GroupService;
import com.cloud.cc.tools.PageHelper;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.Devices;
import com.cloud.cc.vo.Group;
import com.cloud.cc.vo.Users;

@Controller
public class UserDeviceController {

	
	@Autowired
	private GroupService gourpService;
	
	@Autowired
	private DevicesService devicesServiceImpl;
	
	
	/**
	 * 添加设备
	 * @param request udid-设备UDID remark-设备备注
	 * @return
	 */
	@RequestMapping("/addDevices")
	@ResponseBody
	public Map<String,Object> addDevices(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String udid=request.getParameter("udid");
		String remark=request.getParameter("remark");
		Users user=(Users)request.getSession().getAttribute("user");
		if(StringUnits.isEmpty(udid) || StringUnits.isEmpty(remark)){
			resultMap.put("code", 2);	//	参数错误
			return resultMap;
			
		}
		//判断是否有该udid
		if(devicesServiceImpl.selectByUdId(udid,user.getUserid())!=null){
			resultMap.put("code", 3);	//该udid设备已经存在
			return resultMap;
		}
		Devices devices=new Devices();
		devices.setUdid(udid);
		devices.setRemark(remark);
		devices.setCreatetime(new Date());
		devices.setUserid(user.getUserid());
		devices.setLivetime(new Date());
		resultMap.put("code",devicesServiceImpl.addDevice(devices));
		return resultMap;
	}
	
	
	/**
	 * 删除设备
	 * @param request devices-设备数组Id
	 * @return
	 */
	@RequestMapping("/delDevices")
	@ResponseBody
	public Map<String,Object> delDevices(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String devices=request.getParameter("devices");
		if(StringUnits.isEmpty(devices) || devices.length()<1){
			resultMap.put("code", 2);	//参数有误
			return resultMap;
		}
		String[] deviceId=devices.substring(0, devices.length()-1).split(",");
		if(deviceId==null || deviceId.length<0){
			resultMap.put("code", 2);	//参数有误
			return resultMap;
		}
		Integer[] id=new Integer[deviceId.length];
		for (int i = 0; i < deviceId.length; i++) {
			if(StringUnits.isEmpty(deviceId[i]) || !StringUnits.isInteger(deviceId[i])){
				resultMap.put("code", 2);	//参数错误
				return resultMap;
			}
			id[i]=Integer.parseInt(deviceId[i]);
		}
		Users user=(Users)request.getSession().getAttribute("user");
		resultMap.put("code", devicesServiceImpl.delDevice(id, user.getUserid()));
		return resultMap;
	}
	
	/**
	 * 添加分组
	 * @param request groupName-组名称
	 * @return
	 */
	@RequestMapping("/addGroup")
	@ResponseBody
	public Map<String,Object> addGroup(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String groupName=request.getParameter("groupName");
		Users user=(Users)request.getSession().getAttribute("user");
		if(StringUnits.isEmpty(groupName)){
			resultMap.put("code", 2);	//参数有误
			return resultMap;
		}
		Group group=new Group();
		group.setCreatetime(new Date());
		group.setGroupname(groupName);
		group.setUserid(user.getUserid());
		resultMap.put("code", gourpService.addGroupData(group));
		return resultMap;
	}
	
	
	/**
	 * 删除分组
	 * @param request groupId-组Id
	 * @return
	 */
	@RequestMapping("/delGroup")
	@ResponseBody
	public Map<String,Object> delGroup(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String groupId=request.getParameter("groupId");
		Users user=(Users)request.getSession().getAttribute("user");
		if(StringUnits.isEmpty(groupId) || !StringUnits.isInteger(groupId)){
			resultMap.put("code", 2);	//	参数错误
			return resultMap;
		}
		resultMap.put("code", gourpService.delGroup(Integer.parseInt(groupId), true, user.getUserid()));
		return resultMap;
	}
	
	
	/**
	 * 设备分页列表
	 * @param request pageNo-当前页 pageSize-页面容量 udid-根据设备UDID remark-设备名称  groupName-组名称
	 * @return
	 */
	@RequestMapping("/queryPage")
	@ResponseBody
	public Map<String,Object> queryPage(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String pageNo=request.getParameter("pageNo");
		String pageSize=request.getParameter("pageSize");
		String udid=request.getParameter("udid");
		String remark=request.getParameter("remark");
		String groupName=request.getParameter("groupName");
		Users user=(Users)request.getSession().getAttribute("user");
		if(StringUnits.isEmpty(pageSize) || !StringUnits.isInteger(pageSize)){
			pageSize="20";
		}
		if(StringUnits.isEmpty(pageNo) || !StringUnits.isInteger(pageNo)){
			pageSize="1";
		}
		PageHelper<Devices> pageHelper=new PageHelper<Devices>();
		pageHelper.setPageNo(Integer.parseInt(pageNo));
		pageHelper.setPageSize(Integer.parseInt(pageSize));
		pageHelper.getParams().put("udid",udid);
		pageHelper.getParams().put("remark",remark);
		pageHelper.getParams().put("groupName", groupName);
		pageHelper.getParams().put("userId",user.getUserid());
		devicesServiceImpl.queryPage(pageHelper);
		resultMap.put("data",pageHelper);
		resultMap.put("nowDate", new Date());
		return resultMap;
	}
	
	
	/**
	 * 修改设备到组 (移动到分组)
	 * @param request groupId-组Id deviceId-设备Id
	 * @return
	 */
	@RequestMapping("/modifyDeviceGroup")
	@ResponseBody
	public Map<String,Object> modifyDeviceGroup(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String groupId=request.getParameter("groupId");
		String deviceId=request.getParameter("deviceId");
		if(StringUnits.isEmpty(groupId) || !StringUnits.isInteger(groupId)){
			resultMap.put("code", 2);	//参数有误
			return resultMap;
		}
		if(StringUnits.isEmpty(deviceId) || !StringUnits.isInteger(deviceId)){
			resultMap.put("code", 2);	//参数有误
			return resultMap;
		}
		//判断设备和组是否存在
		if(devicesServiceImpl.selectById(Integer.parseInt(deviceId))==null){
			resultMap.put("code", 3);	//该设备不存在，请重试
			return resultMap;
		}
		if(gourpService.selectById(Integer.parseInt(groupId))==null){
			resultMap.put("code", 4);	//该组不存在，请重试 
			return resultMap;
		}
		Devices device=new Devices();
		device.setGroupid(Integer.parseInt(groupId));
		device.setDeviceid(Integer.parseInt(deviceId));
		resultMap.put("code",devicesServiceImpl.updateDevice(device));
		return resultMap;
	}
	
	/**
	 * 获取所有分组数据
	 * @param request
	 * @return
	 */
	@RequestMapping("/getGroupList")
	@ResponseBody
	public Map<String,Object> getGroupList(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Users user=(Users)request.getSession().getAttribute("user");
		resultMap.put("data", gourpService.getGroupListByUser(user.getUserid()));
		return resultMap;
	}
	
	
	/**
	 * 停止设备
	 * @param request devices-设备Id
	 * @return
	 */
	@RequestMapping("/stopDevice")
	@ResponseBody
	public Map<String,Object> stopDevice(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String devices=request.getParameter("devices");
		if(StringUnits.isEmpty(devices) || devices.length()<1){
			resultMap.put("code", 2);
			return resultMap;
		}
		Users user=(Users)request.getSession().getAttribute("user");
		String[] devicesArray=devices.substring(0, devices.length()-1).split(",");
		Integer deviceId[]=new Integer[devicesArray.length];
		for (int i = 0; i < devicesArray.length; i++) {
			deviceId[i]=Integer.parseInt(devicesArray[i]);
		}
		resultMap.put("code", devicesServiceImpl.delDevice(deviceId, user.getUserid()));
		return resultMap;
	}
	
	
	/**
	 * 获取需要授权设备列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/authorizeDeviceList")
	@ResponseBody
	public Map<String,Object> authorizeDeviceList(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Users user=(Users)request.getSession().getAttribute("user");
		resultMap.put("data", devicesServiceImpl.selectByUserId(user.getUserid()));
		return resultMap;
	}
	
	
	/**
	 * 授权设备
	 * @param request dpIds-待授权设备的Id数组
	 * @return
	 */
	@RequestMapping("/authorizeDevice")
	@ResponseBody
	public Map<String,Object> authorizeDevice(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String dpIds=request.getParameter("dpIds");
		if(StringUnits.isEmpty(dpIds)){
			resultMap.put("code", 2);	//请至少选择一个设备
			return resultMap;
		}
		String[] devicesArray=dpIds.substring(0, dpIds.length()-1).split(",");
		Users user=(Users)request.getSession().getAttribute("user");
		resultMap.put("code",devicesServiceImpl.operDevicePoolDevice(devicesArray, user));
		return resultMap;
	}
	
	/**
	 * 拒绝设备授权
	 * @param request dpIds-待授权设备的Id数组
	 * @return
	 */
	@RequestMapping("/delDevicePool")
	@ResponseBody
	public Map<String,Object> delDevicePool(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String dpIds=request.getParameter("dpIds");
		if(StringUnits.isEmpty(dpIds)){
			resultMap.put("code", 2);	//请至少选择一个设备
			return resultMap;
		}
		String[] devicesArray=dpIds.substring(0, dpIds.length()-1).split(",");
		Users user=(Users)request.getSession().getAttribute("user");
		resultMap.put("code",devicesServiceImpl.operDevicePoolDevice(devicesArray, user));
		return resultMap;
	}
	
	
}
