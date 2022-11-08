package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.spring.api.entity.BlockingEntity;
import com.spring.api.entity.FollowingEntity;

public interface UserService{	
	public void createUser(HashMap<String,String> param) throws RuntimeException;

	public void updateMyUserInfo(HttpServletRequest request, HashMap<String, String> param);

	public void createFollowingInfo(HttpServletRequest request, HashMap<String, String> param);
	
	public void createBlockingInfo(HttpServletRequest request, HashMap<String, String> param);

	public void deleteFollowingInfo(HttpServletRequest request, HashMap<String, String> param);

	public void deleteBlockingInfo(HttpServletRequest request, HashMap<String, String> param);

	public List<FollowingEntity> readFollowingInfo(HttpServletRequest request);

	public List<BlockingEntity> readBlockingInfo(HttpServletRequest request);
}