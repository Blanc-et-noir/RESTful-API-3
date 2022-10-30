package com.spring.api.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.entity.QuestionEntity;
import com.spring.api.entity.UserEntity;

@Mapper
public interface UserMapper {
	public void createUser(HashMap param);

	public UserEntity readUserInfoByUserId(HashMap<String, String> param);

	public UserEntity readUserInfoByUserPhone(HashMap<String, String> param);
	
	public QuestionEntity readQuestionByQuestionId(HashMap<String, String> param);

	public void updateMyUserInfo(HashMap<String, String> param);
}