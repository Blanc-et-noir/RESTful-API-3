package com.spring.api.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	public void createUser(HashMap param);

	public HashMap readUserInfoByUserId(HashMap<String, String> param);

	public HashMap readUserInfoByUserPhone(HashMap<String, String> param);
	
	public HashMap readQuestionByQuestionId(HashMap<String, String> param);

	public void updateMyUserInfo(HashMap<String, String> param);
}