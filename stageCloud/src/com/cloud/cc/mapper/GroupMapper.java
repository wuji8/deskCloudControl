package com.cloud.cc.mapper;

import java.util.List;

import com.cloud.cc.vo.Group;

public interface GroupMapper {
    int deleteByPrimaryKey(Integer groupid);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer groupid);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);
    
    
    List<Group> getGroupListByUser(Integer userId);
}