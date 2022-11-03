package com.spring.api.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.entity.BlockingEntity;
import com.spring.api.entity.FollowingEntity;
import com.spring.api.entity.QuestionEntity;
import com.spring.api.entity.UserEntity;

@Mapper
public interface UserMapper {
	public void createUser(HashMap param);

	public UserEntity readUserInfoByUserId(String user_id);

	public UserEntity readUserInfoByUserPhone(String user_phone);
	
	public UserEntity readUserInfoByNotWithdrawedUserId(String user_id);
	
	public QuestionEntity readQuestionByQuestionId(int question_id);

	public void updateMyUserInfo(HashMap<String, String> param);
	
	public void updateUserLoginTime(String user_id);
	
	public void updateUserLogoutTime(String user_id);
	
	public FollowingEntity readFollowingInfoByBothUserId(HashMap param);

	public void createFollowingInfo(HashMap param);

	public List<FollowingEntity> readFollowingInfoBySourceUserId(String source_user_id);

	public BlockingEntity readBlockingInfoByBothUserId(HashMap param);

	public List<BlockingEntity> readBlockingInfoBySourceUserId(String source_user_id);

	public void createBlockingInfo(HashMap param);
}