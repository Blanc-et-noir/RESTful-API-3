package com.spring.api.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.code.UserError;
import com.spring.api.encrypt.SHA;
import com.spring.api.entity.UserEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.CheckUtil;
import com.spring.api.util.RedisUtil;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{
	private final UserMapper userMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final CheckUtil checkUtil;
    private final RedisUtil redisUtil;
	
	@Autowired
	UserServiceImpl(UserMapper userMapper, JwtTokenProvider jwtTokenProvider, CheckUtil checkUtil, RedisUtil redisUtil){
		this.userMapper = userMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.checkUtil = checkUtil;
		this.redisUtil = redisUtil;
	}
	
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
		checkUtil.isUserIdDuplicate(userMapper.readUserInfoByUserId(user_id));
		checkUtil.isUserPhoneDuplicate(userMapper.readUserInfoByUserPhone(user_phone));
		checkUtil.isQuestionExistent(Integer.parseInt(question_id));
		
		String user_salt = SHA.getSalt();
		param.put("user_pw", SHA.DSHA512(user_pw, user_salt));
		param.put("user_salt", user_salt);
		param.put("question_answer", SHA.DSHA512(question_answer.replaceAll(" ", ""), user_salt));
		
		userMapper.createUser(param);
		
		return;
	}

	@Override
	public void updateMyUserInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String new_user_gender = param.get("new_user_gender");
		String new_user_pw = param.get("new_user_pw");
		String new_user_pw_check = param.get("new_user_pw_check");
		String new_user_name = param.get("new_user_name");
		String new_user_phone = param.get("new_user_phone");
		String new_question_id = param.get("new_question_id");
		String new_question_answer = param.get("new_question_answer");
		String question_answer = param.get("question_answer");
		
		UserEntity userEntity = checkUtil.isUserExistent(user_id);
		String user_salt = userEntity.getUser_salt();

		if(new_user_pw!=null&&new_user_pw_check!=null) {
			checkUtil.checkUserPwRegex(new_user_pw);
			checkUtil.checkUserPwRegex(new_user_pw_check);
			checkUtil.checkUserPwAndPwCheck(new_user_pw, new_user_pw_check);
		}
		
		if(new_user_name!=null) {
			checkUtil.checkUserNameRegex(new_user_name);
			param.put("user_name", new_user_name);
		}
		
		if(new_user_gender!=null) {
			checkUtil.checkUserGender(new_user_gender);
			param.put("user_gender", new_user_gender);
		}
		
		if(new_user_phone!=null) {
			checkUtil.checkUserPhoneRegex(new_user_phone);
			checkUtil.isUserPhoneDuplicate(userMapper.readUserInfoByUserPhone(new_user_phone));
			param.put("new_user_phone", new_user_phone);
		}
		
		if(new_user_pw!=null&&new_question_id!=null&&new_question_answer!=null) {
			checkUtil.checkQuestionIdRegex(new_question_id);
			checkUtil.checkQuestionAnswerBytes(new_question_answer);
			checkUtil.checkQuestionAnswerBytes(question_answer);
			checkUtil.checkUserQuestionAnswerAndOldQuestionAnswer(SHA.DSHA512(question_answer.replaceAll(" ", ""), user_salt), userEntity.getQuestion_answer());
			checkUtil.isQuestionExistent(Integer.parseInt(new_question_id));
			
			String new_user_salt = SHA.getSalt();
			param.put("new_question_id", new_question_id);
			param.put("new_user_salt", new_user_salt);
			param.put("new_user_pw", SHA.DSHA512(new_user_pw, new_user_salt));
			param.put("new_question_answer", SHA.DSHA512(new_question_answer.replaceAll(" ", ""), new_user_salt));			
		}else if(new_user_pw==null&&new_question_id==null&&new_question_answer==null) {
			
		}else {
			throw new CustomException(UserError.USER_PW_AND_QUESTION_ID_AND_QUESTION_ANSWER_ARE_NEEDED_AT_THE_SAME_TIME);
		}

		userMapper.updateMyUserInfo(param);
		
		redisUtil.delete(user_id+"_user_accesstoken");
		redisUtil.delete(user_id+"_user_refreshtoken");
		
		return;
	}

	@Override
	public void createFollowingInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String target_user_id = param.get("target_user_id");
		
		param.put("source_user_id", source_user_id);
		
		checkUtil.checkUserIdRegex(target_user_id);
		checkUtil.isSourceUserIdAndTargetUserIdNotSame(source_user_id, target_user_id);
		checkUtil.isUserExistent(source_user_id);
		checkUtil.isUserExistent(target_user_id);
		checkUtil.checkNumberOfFollowingInfo(source_user_id);
		checkUtil.isNotBlocked(source_user_id, target_user_id);
		checkUtil.isNotFollowed(source_user_id, target_user_id);

		userMapper.createFollowingInfo(param);
		
		return;
	}

	@Override
	public void createBlockingInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String target_user_id = param.get("target_user_id");
		
		param.put("source_user_id", source_user_id);
		
		checkUtil.checkUserIdRegex(target_user_id);
		checkUtil.isSourceUserIdAndTargetUserIdNotSame(source_user_id, target_user_id);
		checkUtil.isUserExistent(source_user_id);
		checkUtil.isUserExistent(target_user_id);
		checkUtil.checkNumberOfBlockingInfo(source_user_id);
		checkUtil.isNotBlocked(source_user_id, target_user_id);
		checkUtil.isNotFollowed(source_user_id, target_user_id);

		userMapper.createBlockingInfo(param);
		
		return;
	}

	@Override
	public void deleteFollowingInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String target_user_id = param.get("target_user_id");
		
		param.put("source_user_id", source_user_id);
		checkUtil.checkUserIdRegex(target_user_id);
		checkUtil.isSourceUserIdAndTargetUserIdNotSame(source_user_id, target_user_id);
		checkUtil.isUserExistent(source_user_id);
		checkUtil.isUserExistent(target_user_id);
		checkUtil.isFollowed(source_user_id, target_user_id);
		
		userMapper.deleteFollowingInfoByBothUserId(param);
		
		return;
	}

	@Override
	public void deleteBlockingInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String target_user_id = param.get("target_user_id");
		
		param.put("source_user_id", source_user_id);
		checkUtil.checkUserIdRegex(target_user_id);
		checkUtil.isSourceUserIdAndTargetUserIdNotSame(source_user_id, target_user_id);
		checkUtil.isUserExistent(source_user_id);
		checkUtil.isUserExistent(target_user_id);
		checkUtil.isBlocked(source_user_id, target_user_id);
		
		userMapper.deleteBlockingInfoByBothUserId(param);
		
		return;
	}
}