package com.spring.api.service;

import java.util.HashMap;

public interface AuthService {
	public void getAuthcode(HashMap<String, String> param) throws Exception;
}