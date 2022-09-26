package com.spring.api.service;

import java.util.HashMap;

public interface UserService{	
	public void createUser(HashMap<String,String> param) throws RuntimeException;
}