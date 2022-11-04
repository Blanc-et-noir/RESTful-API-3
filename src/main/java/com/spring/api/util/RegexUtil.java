package com.spring.api.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.Getter;

@Component
@Getter
public class RegexUtil {
	private final String USER_ID_REGEX = "^[a-zA-Z]{1}[a-zA-Z0-9_]{5,15}$";
	private final String USER_PW_REGEX = "[a-zA-Z0-9_]{8,16}$";
	private final String USER_PHONE_REGEX = "\\d{3}-\\d{4}-\\d{4}";
	private final String UUID_REGEX = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";
	private final String USER_NAME_REGEX = "^[가-힣]{2,5}$";
	private final String DATE_REGEX = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
	private final String USER_GENDER_REGEX = "[mfMF]";
	private final int QUESTION_ANSWER_MAX_BYTES = 120;
	
	public boolean checkBytes(String str, final int maxLength) {
		if(!StringUtils.hasText(str)) {
			return false;
		}else if(str.getBytes().length>maxLength) {
			return false;
		}else {
			return true;
		}
	}

	public boolean checkRegex(String str, String regex) {
		if(!StringUtils.hasText(str)) {
			return false;
		}else if(!str.matches(regex)) {
			return false;
		}else {
			return true;
		}
	}
}