package com.spring.api.util;

import java.sql.Timestamp;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class ResultUtil{	
	public HashMap<String,Object> createResultMap(String message, boolean flag) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		result.put("message", message);
		result.put("flag", flag);
		result.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
		return result;
	}
}