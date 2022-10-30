package com.spring.api.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.code.UserError;
import com.spring.api.encrypt.SHA;
import com.spring.api.entity.QuestionEntity;
import com.spring.api.entity.UserEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.CheckUtil;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired
	private UserMapper userMapper;
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private CheckUtil checkUtil;
	
	public void createUser(HashMap<String,String> param) throws CustomException {
		String user_id = param.get("user_id");
		String user_pw = param.get("user_pw");
		String user_pw_check = param.get("user_pw_check");
		String user_name = param.get("user_name");
		String user_phone = param.get("user_phone");
		String question_id = param.get("question_id");
		String question_answer = param.get("question_answer");
		String user_gender = param.get("user_gender");
		
		checkUtil.checkUserIdRegex(user_id);
		checkUtil.checkUserPwRegex(user_pw);
		checkUtil.checkUserPwRegex(user_pw_check);
		checkUtil.checkUserPwAndPwCheck(user_pw, user_pw_check);
		checkUtil.checkUserNameRegex(user_name);
		checkUtil.checkUserPhoneRegex(user_phone);
		checkUtil.checkQuestionIdRegex(question_id);
		checkUtil.checkQuestionAnswerBytes(question_answer);
		checkUtil.checkUserGender(user_gender);
		
		UserEntity userEntity = userMapper.readUserInfoByUserId(param);
		checkUtil.checkUserIsNull(userEntity);

		QuestionEntity questionEntity = userMapper.readQuestionByQuestionId(param);
		checkUtil.checkQuestionIsNotNull(questionEntity);
		
		String user_salt = SHA.getSalt();
		param.put("user_pw", SHA.DSHA512(user_pw, user_salt));
		param.put("user_salt", user_salt);
		param.put("question_answer", SHA.DSHA512(question_answer.replaceAll(" ", ""), user_salt));
		
		userMapper.createUser(param);
		
		return;
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
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		param.put("user_id", user_id);
		UserEntity userEntity = userMapper.readUserInfoByUserId(param);
		
		String old_question_answer = userEntity.getQuestion_answer();
		String old_user_salt = userEntity.getUser_salt();
		
		checkUtil.checkUserIsNotNull(userEntity);
		
		if(new_user_pw!=null&&new_user_pw_check!=null) {
			checkUtil.checkUserPwRegex(new_user_pw);
			checkUtil.checkUserPwRegex(new_user_pw_check);
			checkUtil.checkUserPwAndPwCheck(new_user_pw, new_user_pw_check);
		}
		
		if(new_user_name!=null) {
			checkUtil.checkUserNameRegex(new_user_name);
		}
		
		if(new_user_phone!=null) {
			checkUtil.checkUserPhoneRegex(new_user_phone);
			
			param.put("user_phone", new_user_phone);
			if(userMapper.readUserInfoByUserPhone(param)!=null) {
				throw new CustomException(UserError.DUPLICATE_USER_PHONE);
			}
		}
		
		if(new_user_pw!=null&&new_question_id!=null&&new_question_answer!=null) {
			checkUtil.checkQuestionIdRegex(new_question_id);
			checkUtil.checkQuestionAnswerBytes(new_question_answer);
			checkUtil.checkQuestionAnswerBytes(old_question_answer);
			checkUtil.checkUserQuestionAnswerAndOldQuestionAnswer(SHA.DSHA512(question_answer.replaceAll(" ", ""), old_user_salt), old_question_answer);
			
			param.put("question_id", new_question_id);
			checkUtil.checkQuestionIsNotNull(userMapper.readQuestionByQuestionId(param));
			
			String new_user_salt = SHA.getSalt();
			param.put("new_user_salt", new_user_salt);
			param.put("new_user_pw", SHA.DSHA512(new_user_pw, new_user_salt));
			param.put("new_question_answer", SHA.DSHA512(new_question_answer.replaceAll(" ", ""), new_user_salt));
			
		}else if(new_user_pw==null&&new_question_id==null&&new_question_answer==null) {
			
		}else {
			throw new CustomException(UserError.USER_PW_AND_QUESTION_ID_AND_QUESTION_ANSWER_ARE_NEEDED_AT_THE_SAME_TIME);
		}

		userMapper.updateMyUserInfo(param);
		
		redisTemplate.delete(user_id+"_user_accesstoken");
		redisTemplate.delete(user_id+"_user_refreshtoken");
		
		return;
	}
}