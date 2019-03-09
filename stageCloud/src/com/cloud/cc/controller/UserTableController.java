package com.cloud.cc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cloud.cc.jdbc.JDBC;
import com.cloud.cc.service.UserTableService;
import com.cloud.cc.tools.JsonUtil;
import com.cloud.cc.tools.StringUnits;
import com.cloud.cc.vo.UserTable;


@Controller
public class UserTableController {

	@Autowired
	private UserTableService userTableService;

	
	/**
	 * 根据项目Id获取表
	 * @param request cloudId-项目Id
	 * @return
	 */
	@RequestMapping("/getUserTable")
	@ResponseBody
	public Map<String, Object> getUserTable(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String cloudId = request.getParameter("cloudId");
		if (StringUnits.isEmpty(cloudId) || !StringUnits.isInteger(cloudId)) {
			resultMap.put("code", 2); // 请选择正确的项目
			return resultMap;
		}
		resultMap.put("data", userTableService.selectUserTableByCloudId(Integer.parseInt(cloudId)));
		return resultMap;
	}

	
	/**
	 * 根据表获取字段
	 * @param request tableId-表Id
	 * @return
	 */
	@RequestMapping("/getTableFiled")
	@ResponseBody
	public Map<String, Object> getTableFiled(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String tableId = request.getParameter("tableId");
		if (StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)) {
			resultMap.put("code", 2); // 没有选表
			return resultMap;
		}
		UserTable userTable = userTableService.selectUserTableByTableId(Integer.parseInt(tableId));
		resultMap.put("data", userTableService.selectTableFieldList(userTable.getDbtable()));
		return resultMap;
	}

	
	/**
	 * 添加表数据 
	 * @param request fields-数据json格式 tableid-表Id
	 * @return
	 */
	@RequestMapping("/addTableData")
	@ResponseBody
	public Map<String, Object> addTableData(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String tableId = request.getParameter("tableid");
		String fields = request.getParameter("fields");
		if (StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)) {
			resultMap.put("code", 2); // 请选择正确的表
			return resultMap;
		}
		if (StringUnits.isEmpty(fields)) {
			resultMap.put("code", 3);
			return resultMap;
		}
		// 得出表对象
		UserTable userTable = userTableService.selectUserTableByTableId(Integer.parseInt(tableId));
		Map<String, String> map = JsonUtil.jsonToMapString(fields);
		String sql = "insert into " + userTable.getDbtable() + "(";
		StringBuilder builder = new StringBuilder(sql);
		int i=0;
		for (String key : map.keySet()) {
			String value = map.get(key);
			if (value == null) { // 过滤空的key
				continue;
			}
			 if (i != 0) {
	                builder.append(',');
	            }
			 builder.append("`"+key+"`");
			 i++;
		}
		builder.append(") value(");
		i=0;
		for (String key : map.keySet()) {
			String value = map.get(key);
			if (value == null) { // 过滤空的key
				continue;
			}
			 if (i != 0) {
	                builder.append(',');
	            }
			 builder.append("'"+map.get(key)+"'");
			 i++;
		}
		builder.append(");");
		resultMap.put("code",JDBC.upDate(builder.toString())?1:0);
		return resultMap;
	}
	
	/**
	 * 获取表数据 根据条件查询
	 * @param request key-条件字段数据 val-值字段数组 tableid-表Id pagesize-数量 pindex-页码
	 * @return
	 */
	@RequestMapping("/getTableData")
	@ResponseBody
	public Map<String,Object> getTableData(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		System.out.println(StringUnits.getParam(request));
		String[] key=request.getParameterValues("key");
		String[] val=request.getParameterValues("val");
		String tableId=request.getParameter("tableid");
		String pageSize=request.getParameter("pagesize");
		String pindex=request.getParameter("pindex");
		if(StringUnits.isEmpty(pindex) || !StringUnits.isInteger(pindex)){
			resultMap.put("code", 3);	//页码参数请检查
			return resultMap;
		}
		if(StringUnits.isEmpty(pageSize) || !StringUnits.isInteger(pageSize)){
			resultMap.put("code", 3);	//页码参数请检查
			return resultMap;
		}
		if(StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)){
			resultMap.put("code", 4);	//请检查表 
			return resultMap;
		}
		UserTable userTable = userTableService.selectUserTableByTableId(Integer.parseInt(tableId));
		String sql="select * from "+userTable.getDbtable();
		if(key!=null && key.length>0){
			if(key.length!=val.length){
				resultMap.put("code", 2);	//条件数据有问题，请检查
				return resultMap;
			}
			sql+=" where 1=1";
			for (int i = 0; i < key.length; i++) {
				sql+=" and `"+key[i]+"`='"+val[i]+"'";
			}
		}
		sql+=" limit "+(Integer.parseInt(pindex)-1)+","+Integer.parseInt(pageSize);
		resultMap.put("data",JDBC.getListData(sql));
		resultMap.put("code", 1);
		return resultMap;
	}
	
	
	/**
	 * 修改表数据
	 * @param request tableid-表Id  fields-数据json格式
	 * @return
	 */
	@RequestMapping("/updateTableData")
	@ResponseBody
	public Map<String,Object> updateTableData(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String id=request.getParameter("id");
		String tableId=request.getParameter("tableid");
		String fields=request.getParameter("fields");
		if(StringUnits.isEmpty(id) || !StringUnits.isInteger(id)){
			resultMap.put("code", 2);	//请选择正确的id
			return resultMap;
		}
		if(StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)){
			resultMap.put("code",3);	//请选择正确的表
			return resultMap;
		}
		if(StringUnits.isEmpty(fields)){
			resultMap.put("code", 4);	//数据有误
			return resultMap;
		}
		// 得出表对象
				UserTable userTable = userTableService.selectUserTableByTableId(Integer.parseInt(tableId));
				Map<String, String> map = JsonUtil.jsonToMapString(fields);
				String sql = "update " + userTable.getDbtable() + " set ";
				StringBuilder builder = new StringBuilder(sql);
				int i=0;
				for (String key : map.keySet()) {
					String value = map.get(key);
					if (value == null) { // 过滤空的key
						continue;
					}
					 if (i != 0) {
			                builder.append(',');
			            }
					 builder.append("`"+key+"`="+map.get(key));
					 i++;
				}
		resultMap.put("code",JDBC.upDate(builder.toString())?1:0);
		return resultMap;
	}
	
	
	/**
	 * 删除表数据
	 * @param request tableid-表id  删除多条数据时-id要用,隔开
	 * @return
	 */
	@RequestMapping("/delData")
	@ResponseBody
	public Map<String,Object> delData(HttpServletRequest request){
		Map<String,Object> resultMap=new HashMap<String, Object>();
		String tableId=request.getParameter("tableid");
		String id=request.getParameter("id");
		if(StringUnits.isEmpty(tableId) || !StringUnits.isInteger(tableId)){
			resultMap.put("code", 2);	//请检查该表 参数不正确
			return resultMap;
		}
		if(StringUnits.isEmpty(id)){
			resultMap.put("code", 3);	//请检查该ID是否正确
			return resultMap;
		}
		UserTable userTable = userTableService.selectUserTableByTableId(Integer.parseInt(tableId));
		String sql="delete from "+userTable.getDbtable()+" where id in("+id+")";
		resultMap.put("code", JDBC.upDate(sql)?1:0);
		return resultMap;
	}
}
