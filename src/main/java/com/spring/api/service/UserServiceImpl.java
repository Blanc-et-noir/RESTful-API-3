package com.spring.api.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.code.UserError;
import com.spring.api.encrypt.SHA;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.RegexUtil;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired
	UserMapper userMapper;
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
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
		
		HashMap user = userMapper.readUserInfoByUserId(param);
		
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

	@Override
	public void updateMyUserInfo(HttpServletRequest request, HashMap<String, String> param) {
		String new_user_pw = param.get("new_user_pw");
		String new_user_pw_check = param.get("new_user_pw_check");
		String new_user_name = param.get("new_user_name");
		String new_user_phone = param.get("new_user_phone");
		String new_question_id = param.get("new_question_id");
		String new_question_answer = param.get("new_question_answer");
		
		String question_answer = param.get("question_answer");
		
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = JwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		param.put("user_id", user_id);
		
		HashMap<String,String> user = userMapper.readUserInfoByUserId(param);
		
		String old_question_answer = user.get("question_answer");
		String old_user_salt = user.get("user_salt");
		
		if(user==null) {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
		
		if((new_user_pw!=null&&RegexUtil.checkRegex(new_user_pw, RegexUtil.USER_PW_REGEX))||new_user_pw==null) {
			
		}else {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}
		
		if((new_user_pw_check!=null&&RegexUtil.checkRegex(new_user_pw_check, RegexUtil.USER_PW_REGEX))||new_user_pw_check==null) {
			
		}else {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}
		
		if(new_user_pw==null&&new_user_pw_check==null) {
			
		}else if(new_user_pw!=null&&new_user_pw_check!=null){
			if(!new_user_pw.equals(new_user_pw_check)) {
				throw new CustomException(UserError.USER_PW_NOT_EQUAL_TO_USER_PW_CHECK);
			}
		}
		
		
		
		if((new_user_name!=null&&RegexUtil.checkRegex(new_user_name, RegexUtil.USER_NAME_REGEX))||new_user_name==null) {
			
		}else {
			throw new CustomException(UserError.USER_NAME_NOT_MATCHED_TO_REGEX);
		}
		
		if((new_user_phone!=null&&RegexUtil.checkRegex(new_user_phone, RegexUtil.USER_PHONE_REGEX))||new_user_phone==null) {
			param.put("user_phone", new_user_phone);
			user = userMapper.readUserInfoByUserPhone(param);
			
			if(user!=null) {
				throw new CustomException(UserError.DUPLICATE_USER_PHONE);
			}
		}else {
			throw new CustomException(UserError.USER_PHONE_NOT_MATCHED_TO_REGEX);
		}
		
		
		if((new_question_id!=null&&RegexUtil.checkRegex(new_question_id, RegexUtil.UUID_REGEX))||new_question_id==null) {
			param.put("question_id", new_question_id);
			
			if(new_question_id!=null) {
				HashMap<String,String> question = userMapper.readQuestionByQuestionId(param);
				
				if(question==null) {
					throw new CustomException(UserError.INVALID_QUESTION_ID);
				}
			}
		}else {
			throw new CustomException(UserError.QUESTION_ID_NOT_MATCHED_TO_REGEX);
		}
		
		if((new_question_answer!=null&&RegexUtil.checkBytes(new_question_answer, RegexUtil.QUESTION_ANSWER_MAX_BYTES))||new_question_answer==null) {

		}else {
			throw new CustomException(UserError.QUESTION_ANSWER_EXCEED_MAX_BYTES);
		}
		
		if(!((new_user_pw==null&&new_question_id==null&&new_question_answer==null)||(new_user_pw!=null&&new_question_id!=null&&new_question_answer!=null))) {
			throw new CustomException(UserError.USER_PW_AND_QUESTION_ID_AND_QUESTION_ANSWER_ARE_NEEDED_AT_THE_SAME_TIME);
		}else if(new_user_pw!=null&&new_question_id!=null&&new_question_answer!=null){
			String new_user_salt = SHA.getSalt();
			param.put("new_user_salt", new_user_salt);
			param.put("new_user_pw", SHA.DSHA512(new_user_pw, new_user_salt));
			param.put("new_question_answer", SHA.DSHA512(new_question_answer.replaceAll(" ", ""), new_user_salt));
		}
		
		if(question_answer==null) {
			throw new CustomException(UserError.QUESTION_ANSWER_IS_NEEDED);
		}else if(!RegexUtil.checkBytes(question_answer, RegexUtil.QUESTION_ANSWER_MAX_BYTES)) {
			throw new CustomException(UserError.QUESTION_ANSWER_EXCEED_MAX_BYTES);
		}
		
		if(!old_question_answer.equals(SHA.DSHA512(question_answer.replaceAll(" ", ""), old_user_salt))) {
			throw new CustomException(UserError.QUESTION_ANSWER_NOT_EQUAL_TO_OLD_QUESTION_ANSWER);
		}

		userMapper.updateMyUserInfo(param);
		
		redisTemplate.delete(user_id+"_user_accesstoken");
		redisTemplate.delete(user_id+"_user_refreshtoken");
	}
}