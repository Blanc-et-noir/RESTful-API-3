package com.spring.api.util;

import org.springframework.util.StringUtils;

public class RegexUtil {
	public final static String USER_ID_REGEX = "^[a-zA-Z]{1}[a-zA-Z0-9_]{5,15}$";
	public final static String USER_PW_REGEX = "[a-zA-Z0-9_]{8,16}$";
	public final static String USER_PHONE_REGEX = "\\d{3}-\\d{4}-\\d{4}";
	public final static String UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";
	public final static String USER_NAME_REGEX = "^[가-힣]{2,5}$";
	public final static String DATE_REGEX = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
	
	public final static int QUESTION_ANSWER_MAX_BYTES = 120;
	
	
	public static boolean checkBytes(String str, final int maxLength) {
		if(!StringUtils.hasText(str)) {
			return false;
		}else if(str.getBytes().length>maxLength) {
			return false;
		}else {
			return true;
		}
	}

	public static boolean checkRegex(String str, String regex) {
		if(!StringUtils.hasText(str)) {
			return false;
		}else if(!str.matches(regex)) {
			return false;
		}else {
			return true;
		}
	}
}