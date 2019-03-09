package com.cloud.cc.tools;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.cloud.cc.vo.model.SearchModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

/**
 * json操作工具�?
 * @author tansanlin
 *
 */
public class JsonUtil {

	/**
	 * map集合转换成json
	 * @param map--map集合
	 * @return
	 */
	public static String mapToJson(Map<String, Object> map){
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
		return json;
	}
	
	/**
	 * json转换成map集合
	 * @param json--要转换的json数据
	 * @return
	 */
	public static Map<String, Object> jsonToMap(String json){
		Gson gson = new Gson();
		Map<String,Object> map = gson.fromJson(json, new TypeToken<HashMap<String,Object>>(){}.getType());
		return map;
	}
	
	/**
	 * json转map（字符串�?
	 */
	public static Map<String, String> jsonToMapString(String json){
		Gson gson = new Gson();
		Map<String,String> map = gson.fromJson(json, new TypeToken<HashMap<String,Object>>(){}.getType());
		return map;
	}
	
	public static void main(String[] args) {
		String search="[{ key: \"\", val: \"\" }]";
		JSONArray array=JSONArray.parseArray(search);
		for(int i=0;i<array.size();i++){
			com.alibaba.fastjson.JSONObject obj=array.getJSONObject(i);
			System.out.println("key="+obj.getString("key"));
			System.out.println("val="+obj.getString("val"));
		}
	}
}
