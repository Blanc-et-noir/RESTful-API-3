package com.spring.api.util;

import java.util.HashMap;

public class ResultUtil{
	public static HashMap createResultMap(String message, boolean flag) {
		HashMap result = new HashMap();
		result.put("message", message);
		result.put("flag", flag);
		result.put("timestamp", TimeUtil.getTimestamp());
		return result;
	}
}