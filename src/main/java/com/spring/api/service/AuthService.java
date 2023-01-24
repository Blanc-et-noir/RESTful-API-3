package com.spring.api.service;

import java.util.HashMap;

public interface AuthService {
	public String getVerficationcode(HashMap<String, String> param);
	public void getAuthcode(HashMap<String, String> param) throws Exception;
}