package com.spring.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.spring.api.code.AuthError;
import com.spring.api.code.UserError;
import com.spring.api.entity.QuestionEntity;
import com.spring.api.entity.UserEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.UserMapper;

@Component
public class CheckUtil {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private UserMapper userMapper;
	
	public void checkAccessToken(String stored_user_accesstoken, String user_accesstoken) {
		if(!StringUtils.hasText(user_accesstoken)) {
			throw new CustomException(AuthError.NOT_FOUND_USER_ACCESSTOKEN);
		}else if(!jwtTokenProvider.validateToken(user_accesstoken)) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}else if(!jwtTokenProvider.getTokenType(user_accesstoken).equals("user_accesstoken")) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}else if(stored_user_accesstoken==null) {
			throw new CustomException(AuthError.NOT_FOUND_STORED_USER_ACCESSTOKEN);
		}else if(!stored_user_accesstoken.equals(user_accesstoken)) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}
	}
	
	public void checkRefreshToken(String stored_user_refreshtoken, String user_refreshtoken) {
		if(!StringUtils.hasText(user_refreshtoken)) {
			throw new CustomException(AuthError.NOT_FOUND_USER_REFRESHTOKEN);
		}else if(!jwtTokenProvider.validateToken(user_refreshtoken)) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}else if(!jwtTokenProvider.getTokenType(user_refreshtoken).equals("user_refreshtoken")) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}else if(stored_user_refreshtoken==null) {
			throw new CustomException(AuthError.NOT_FOUND_STORED_USER_REFRESHTOKEN);
		}else if(!stored_user_refreshtoken.equals(user_refreshtoken)) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}
	}
	
	public void checkUserIdRegex(String user_id) {
		if(!RegexUtil.checkRegex(user_id, RegexUtil.USER_ID_REGEX)) {
			throw new CustomException(UserError.USER_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserPwRegex(String user_pw) {
		if(!RegexUtil.checkRegex(user_pw, RegexUtil.USER_PW_REGEX)) {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserPwAndPwCheck(String user_pw, String user_pw_check) {		
		if(!user_pw.equals(user_pw_check)) {
			throw new CustomException(UserError.USER_PW_NOT_EQUAL_TO_USER_PW_CHECK);
		}
	}
	
	public void checkUserGender(String user_gender) {
		if(!RegexUtil.checkRegex(user_gender, RegexUtil.USER_GENDER_REGEX)) {
			throw new CustomException(UserError.USER_GENDER_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserQuestionAnswerAndOldQuestionAnswer(String question_answer, String old_question_answer) {		
		if(!question_answer.equals(old_question_answer)) {
			throw new CustomException(UserError.QUESTION_ANSWER_NOT_EQUAL_TO_OLD_QUESTION_ANSWER);
		}
	}
	
	public void checkUserNameRegex(String user_name) {
		if(!RegexUtil.checkRegex(user_name, RegexUtil.USER_NAME_REGEX)) {
			throw new CustomException(UserError.USER_NAME_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserPhoneRegex(String user_phone) {
		if(!RegexUtil.checkRegex(user_phone, RegexUtil.USER_PHONE_REGEX)) {
			throw new CustomException(UserError.USER_PHONE_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkQuestionIdRegex(String question_id) {
		try {
			Integer.parseInt(question_id);
		}catch(Exception e) {
			throw new CustomException(UserError.QUESTION_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkQuestionAnswerBytes(String question_answer) {
		if(!RegexUtil.checkBytes(question_answer, RegexUtil.QUESTION_ANSWER_MAX_BYTES)) {
			throw new CustomException(UserError.QUESTION_ANSWER_EXCEED_MAX_BYTES);
		}
	}
	
	public UserEntity isUserExistent(String user_id) {		
		UserEntity userEntity = userMapper.readUserInfoByNotWithdrawedUserId(user_id);
		
		if(userEntity==null) {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
		
		return userEntity;
	}
	
	public void isUserIdDuplicate(UserEntity userEntity) {
		if(userEntity!=null) {
			throw new CustomException(UserError.DUPLICATE_USER_ID);
		}
	}
	
	public void isUserPhoneDuplicate(UserEntity userEntity) {
		if(userEntity!=null) {
			throw new CustomException(UserError.DUPLICATE_USER_PHONE);
		}
	}
	
	public void isQuestionExistent(int question_id) {
		QuestionEntity questionEntity = userMapper.readQuestionByQuestionId(question_id);
		
		if(questionEntity==null) {
			throw new CustomException(UserError.NOT_FOUND_QUESTION);
		}
	}
}
