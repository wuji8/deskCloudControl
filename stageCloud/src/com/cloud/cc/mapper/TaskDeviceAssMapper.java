package com.cloud.cc.mapper;

import org.apache.ibatis.annotations.Param;

import com.cloud.cc.vo.TaskDeviceAss;

public interface TaskDeviceAssMapper {
    int deleteByPrimaryKey(Integer tdaid);

    int insert(TaskDeviceAss record);

    int insertSelective(TaskDeviceAss record);

    TaskDeviceAss selectByPrimaryKey(Integer tdaid);

    int updateByPrimaryKeySelective(TaskDeviceAss record);

    int updateByPrimaryKey(TaskDeviceAss record);
    
    int delTaskDeviceAssByDevice(Integer deviceId);
    
    TaskDeviceAss selectByDeviceIdUserId(@Param("deviceId")Integer deviceId,@Param("userId")Integer userId);
}