package com.cloud.cc.service;

import java.util.List;

import com.cloud.cc.vo.Group;

public interface GroupService {
	
	/**
	 * ���ӷ�������
	 * @param group
	 * @return
	 */
	int addGroupData(Group group);
	
	/**
	 * ɾ����������
	 * @param groupId
	 * @param flag	�Ƿ�ɾ�������µ��豸 trueΪɾ��
	 * @param userId
	 * @return
	 */
	int delGroup(Integer groupId,boolean flag,Integer userId);
	
	
	Group selectById(Integer groupId);
	
	
	List<Group> getGroupListByUser(Integer userId);
}
