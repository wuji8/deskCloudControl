package com.cloud.cc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.cc.service.CloudProjectsService;
import com.cloud.cc.service.LogsService;
import com.cloud.cc.service.UsersService;
import com.cloud.cc.tools.PageHelper;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.Logs;
import com.cloud.cc.vo.Users;

@Controller
public class UserController {

	
	@Autowired
	private UsersService userService;
	
	@Autowired
	private LogsService logsService;
	
	@Autowired
	private CloudProjectsService cloudProjectService;
	
	
	/**
	 * 获取当前用户所有项目
	 * @param request
	 * @return
	 */
	@RequestMapping("/getAllProject")
	@ResponseBody
	public Map<String,Object> getAllProject(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Users user=(Users)request.getSession().getAttribute("user");
		if(user.getUserid()==1){
			resultMap.put("data", cloudProjectService.selectAllProjects());
		}else{
			resultMap.put("data", cloudProjectService.selectById(user.getCloudid()));
		}
		return resultMap;
	}
	
	
	/**
	 * 用户登录
	 * @param request userName-用户名称 userPwd-用户密码
	 * @return
	 */
	@RequestMapping("/toLogin")
	@ResponseBody
	public Map<String,Object> isLogin(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String userName=request.getParameter("userName");
		String userPwd=request.getParameter("userPwd");
		if(StringUnits.isEmpty(userPwd) || StringUnits.isEmpty(userName)) {
			resultMap.put("code", 2);	//账号和密码不能为空
			return resultMap;
		}
		Users user=userService.isLogin(userName, userPwd);
		if(user!=null) {	//登录成功
			if(user.getStatus()!=1) {	//被封禁 
				resultMap.put("code", 3);
				return resultMap;
			}
			//进行查找该用户是否有项目
			if(user.getRoleId()!=1){
				if(user.getCloudid()!=null){
					if(cloudProjectService.selectById(user.getCloudid())==null){
						resultMap.put("code", 4);	//还未项目
						return resultMap;
					}
				}else{
					resultMap.put("code", 4);	//还未项目
					return resultMap;
				}
			}
			request.getSession().setAttribute("user", user);
			resultMap.put("code", 1);
			//resultMap.put("data", userService.selectUserRole(user.getUserid(),user.getRoleId()));
		}else {
			resultMap.put("code", 0);
		}
		return resultMap;
	}
	
	/**
	 * 日志分页
	 * @param request type-类型
	 * @return
	 */
	@RequestMapping("/logsPage")
	@ResponseBody
	public Map<String,Object> queryLogsPage(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		Users user=(Users) request.getSession().getAttribute("user");
		String pageNo=request.getParameter("pageNo");
		String pageSize=request.getParameter("pageSize");
		String type=request.getParameter("type");
		if(StringUnits.isEmpty(pageNo) || StringUnits.isEmpty(pageSize)) {
			pageNo="0";
			pageSize="20";
		}
		if(!StringUnits.isInteger(pageSize) || !StringUnits.isInteger(pageNo)) {
			resultMap.put("code", 2);	//页数或页码必须要为数字
			return resultMap;
		}
		PageHelper<Logs> pageHelper=new PageHelper<Logs>();
		pageHelper.setPageNo(Integer.parseInt(pageNo));
		pageHelper.setPageSize(Integer.parseInt(pageSize));
		pageHelper.getParams().put("type", type);
		pageHelper.getParams().put("userId", user.getUserid());
		logsService.queryPage(pageHelper);
		resultMap.put("code", 1);
		resultMap.put("data",pageHelper);
		return resultMap;
	}
	
}
