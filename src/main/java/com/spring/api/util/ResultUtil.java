package com.spring.api.util;

import java.util.HashMap;

public class ResultUtil{
	public static HashMap createResultMap(String message) {
		HashMap result = new HashMap();
		result.put("flag", true);
		result.put("message", message);
		result.put("timestamp", TimeUtil.getTimestamp());
		return result;
	}
}