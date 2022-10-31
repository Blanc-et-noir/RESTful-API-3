package com.spring.api.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

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
}