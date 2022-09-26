package com.spring.api.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.code.UserError;
import com.spring.api.encrypt.SHA;
import com.spring.api.exception.CustomException;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.RegexUtil;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired
	UserMapper userMapper;
	
	public void createUser(HashMap<String,String> param) throws CustomException {
		String user_id = param.get("user_id");
		String user_pw = param.get("user_pw");
		String user_pw_check = param.get("user_pw_check");
		String user_name = param.get("user_name");
		String user_phone = param.get("user_phone");
		String question_id = param.get("question_id");
		String question_answer = param.get("question_answer");
		
		if(!RegexUtil.checkRegex(user_id, RegexUtil.USER_ID_REGEX)) {
			throw new CustomException(UserError.USER_ID_NOT_MATCHED_TO_REGEX);
		}
		
		HashMap user = userMapper.readUserByUserId(param);
		
		if(user!=null) {
			throw new CustomException(UserError.DUPLICATE_USER_ID);
		}
		
		if(!RegexUtil.checkRegex(user_pw, RegexUtil.USER_PW_REGEX)) {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}

		if(!RegexUtil.checkRegex(user_pw_check, RegexUtil.USER_PW_REGEX)) {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}
		
		if(!user_pw.equals(user_pw_check)) {
			throw new CustomException(UserError.USER_PW_NOT_EQUAL_TO_USER_PW_CHECK);
		}
		
		String user_salt = SHA.getSalt();
		param.put("user_pw", SHA.DSHA512(user_pw, user_salt));
		param.put("user_salt", user_salt);
		
		if(!RegexUtil.checkRegex(user_name, RegexUtil.USER_NAME_REGEX)) {
			throw new CustomException(UserError.USER_NAME_NOT_MATCHED_TO_REGEX);
		}
		
		if(!RegexUtil.checkRegex(user_phone, RegexUtil.USER_PHONE_REGEX)) {
			throw new CustomException(UserError.USER_PHONE_NOT_MATCHED_TO_REGEX);
		}
		
		if(!RegexUtil.checkRegex(question_id, RegexUtil.UUID_REGEX)) {
			throw new CustomException(UserError.QUESTION_ID_NOT_MATCHED_TO_REGEX);
		}
		
		HashMap question = userMapper.readQuestionByQuestionId(param);
		
		if(question==null) {
			throw new CustomException(UserError.INVALID_QUESTION_ID);
		}
		
		if(!RegexUtil.checkBytes(question_answer, RegexUtil.QUESTION_ANSWER_MAX_BYTES)) {
			throw new CustomException(UserError.QUESTION_ANSWER_EXCEED_MAX_BYTES);
		}
		
		param.put("question_answer", SHA.DSHA512(question_answer.replaceAll(" ", ""), user_salt));
		
		userMapper.createUser(param);
	}
}