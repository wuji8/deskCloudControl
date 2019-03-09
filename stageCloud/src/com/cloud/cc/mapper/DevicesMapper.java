package com.cloud.cc.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cloud.cc.vo.Devices;

public interface DevicesMapper {
    int deleteByPrimaryKey(Integer deviceid);

    int insert(Devices record);

    int insertSelective(Devices record);

    Devices selectByPrimaryKey(Integer deviceid);

    int updateByPrimaryKeySelective(Devices record);

    int updateByPrimaryKey(Devices record);
    
    int delByGroupId(Integer groupId);
    
    List<Devices> selectByGroupId(Integer groupId);
    
    List<Devices> queryPage(Map<String,Object> param);
    
    int countPage(Map<String,Object> param);
    
    Devices selectByUdId(@Param("udId")String udId,@Param("userId")Integer userId);
}