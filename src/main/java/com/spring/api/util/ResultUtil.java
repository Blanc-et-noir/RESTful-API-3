package com.spring.api.util;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultUtil{
	private final TimeUtil timeUtil;
	
	@Autowired
	ResultUtil(TimeUtil timeUtil){
		this.timeUtil = timeUtil;
	}
	
	public HashMap createResultMap(String message, boolean flag) {
		HashMap result = new HashMap();
		result.put("message", message);
		result.put("flag", flag);
		result.put("timestamp", timeUtil.getTimestamp());
		return result;
	}
}