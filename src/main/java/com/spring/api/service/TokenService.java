package com.spring.api.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.api.dto.UserDTO;

public interface TokenService{	
	public void createToken(HttpServletRequest request, HttpServletResponse response, HashMap<String,String> param) throws RuntimeException;

	public void deleteToken(HttpServletRequest request);

	public void updateToken(HttpServletRequest request, HttpServletResponse response);

	public HashMap readToken(HttpServletRequest request);
}