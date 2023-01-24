package com.spring.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.Getter;

@Component
@Getter
public class RegexUtil {
	private final String USER_ID_REGEX = "^[a-zA-Z]{1}[a-zA-Z0-9_]{5,15}$";
	private final String USER_PW_REGEX = "[a-zA-Z0-9_]{8,16}$";
	private final String HASHTAG_CONTENT_REGEX = "[a-zA-Zㄱ-힣0-9\s]{1,20}$";
	private final String USER_PHONE_REGEX = "\\d{3}-\\d{4}-\\d{4}";
	private final String USER_NAME_REGEX = "^[가-힣]{2,5}$";
	private final String DATE_REGEX = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
	private final String USER_GENDER_REGEX = "[mfMF]";
	private final String AUTHCODE_REGEX;
	private final String VERIFICATIONCODE_REGEX;
	
	private final int QUESTION_ANSWER_MAX_BYTES = 120;
	private final int MESSAGE_TITLE_MAX_BYTES = 120;
	private final int MESSAGE_CONTENT_MAX_BYTES = 600;
	private final int ITEM_NAME_MAX_BYTES = 120;
	private final int ITEM_DESCRIPTION_MAX_BYTES = 3000;
	private final int HASHTAG_CONTENT_MAX_BYTES = 60;
	private final int COMMENT_CONTENT_MAX_BYTES = 600;	
	private final int SEARCH_MAX_BYTES = 120;
	
	private final int MAX_LIMIT;
	private final int MIN_LIMIT;
	private final int AUTHCODE_DIGIT;
	private final int VERIFICATIONCODE_DIGIT;
	
	@Autowired
	RegexUtil(
		@Value("${min.limit}") int MIN_LIMIT,
		@Value("${max.limit}") int MAX_LIMIT,
		@Value("${sms.auth.digit}") int AUTHCODE_DIGIT,
		@Value("${sms.verification.digit}") int VERIFICATIONCODE_DIGIT
	){
		this.MIN_LIMIT = MIN_LIMIT;
		this.MAX_LIMIT = MAX_LIMIT;
		this.AUTHCODE_DIGIT = AUTHCODE_DIGIT;
		this.VERIFICATIONCODE_DIGIT = VERIFICATIONCODE_DIGIT;
		this.AUTHCODE_REGEX = "[0-9]{"+AUTHCODE_DIGIT+"}";
		this.VERIFICATIONCODE_REGEX = "[0-9]{"+VERIFICATIONCODE_DIGIT+"}";
	}
	
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