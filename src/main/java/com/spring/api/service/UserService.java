package com.spring.api.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public interface UserService{	
	public void createUser(HashMap<String,String> param) throws RuntimeException;

	public void updateMyUserInfo(HttpServletRequest request, HashMap<String, String> param);

	public void createFollowingInfo(HttpServletRequest request, HashMap<String, String> param);
	
	public void createBlockingInfo(HttpServletRequest request, HashMap<String, String> param);

	public void deleteFollowingInfo(HttpServletRequest request, HashMap<String, String> param);

	public void deleteBlockingInfo(HttpServletRequest request, HashMap<String, String> param);
}