package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.spring.api.dto.BlockingDTO;
import com.spring.api.dto.QuestionDTO;
import com.spring.api.dto.UserDTO;

public interface UserService{	
	public void createUser(HashMap<String,String> param) throws RuntimeException;
	public void updateMyUserInfo(HttpServletRequest request, HashMap<String, String> param);
	public void createBlockingInfo(HttpServletRequest request, HashMap<String, String> param);
	public void deleteBlockingInfo(HttpServletRequest request, HashMap<String, String> param);
	public List<BlockingDTO> readBlockingInfo(HttpServletRequest request);
	public QuestionDTO readQuestion(HttpServletRequest request, HashMap<String,String> param);
	public List<QuestionDTO> readQuestions();
	public void updateUserPw(HttpServletRequest request, HashMap<String, String> param);
	public UserDTO readMyUserInfo(HttpServletRequest request);
	public void deleteMyUserInfo(HttpServletRequest request);
}