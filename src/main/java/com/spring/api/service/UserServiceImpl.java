package com.spring.api.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.code.AuthError;
import com.spring.api.code.UserError;
import com.spring.api.dto.BlockingDTO;
import com.spring.api.dto.QuestionDTO;
import com.spring.api.dto.UserDTO;
import com.spring.api.encrypt.SHA;
import com.spring.api.entity.BlockingEntity;
import com.spring.api.entity.QuestionEntity;
import com.spring.api.entity.UserEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.RedisUtil;
import com.spring.api.util.UserCheckUtil;

@Service("userService")
@Transactional(rollbackFor= {Exception.class})
public class UserServiceImpl implements UserService{
	private final UserMapper userMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserCheckUtil userCheckUtil;
    private final RedisUtil redisUtil;
    private final String PREFIX_AUTH_CODE = "auth_code:";
    private final String PREFIX_VERIFICATION_CODE = "verification_code:";
	@Autowired
	UserServiceImpl(UserMapper userMapper, JwtTokenProvider jwtTokenProvider, UserCheckUtil checkUtil, RedisUtil redisUtil){
		this.userMapper = userMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userCheckUtil = checkUtil;
		this.redisUtil = redisUtil;
	}
	
	@Override
	public String findUserId(HashMap<String,String> param) {
		String user_phone = param.get("user_phone");
		String verification_code = param.get("verification_code");
		String stored_verification_code = redisUtil.getData(PREFIX_VERIFICATION_CODE+user_phone);
		
		userCheckUtil.checkUserVerificationcodeRegex(verification_code);
		
		if(stored_verification_code==null) {
			throw new CustomException(AuthError.NOT_FOUND_VERIFICATION_CODE);
		}

		userCheckUtil.checkVerificationcodeAndStoredVerificationcode(stored_verification_code,verification_code);
		
		return userMapper.readUserInfoByNotWithdrawedUserPhone(user_phone).getUser_id();
	}
	
	@Override
	public void createUser(HashMap<String,String> param){
		String user_id = param.get("user_id");
		String user_pw = param.get("user_pw");
		String user_pw_check = param.get("user_pw_check");
		String user_name = param.get("user_name");
		String user_phone = param.get("user_phone");
		String question_id = param.get("question_id");
		String question_answer = param.get("question_answer");
		String user_gender = param.get("user_gender");
		String verification_code = param.get("verification_code");
		
		userCheckUtil.checkUserIdRegex(user_id);
		userCheckUtil.checkUserPwRegex(user_pw);
		userCheckUtil.checkUserPwRegex(user_pw_check);
		userCheckUtil.checkUserPwAndPwCheck(user_pw, user_pw_check);
		userCheckUtil.checkUserNameRegex(user_name);
		userCheckUtil.checkUserPhoneRegex(user_phone);
		userCheckUtil.checkQuestionIdRegex(question_id);
		userCheckUtil.checkQuestionAnswerBytes(question_answer);
		userCheckUtil.checkUserGender(user_gender);
		userCheckUtil.checkUserAuthcodeRegex(verification_code);
		
		String stored_verification_code = redisUtil.getData(PREFIX_VERIFICATION_CODE+user_phone);
		
		if(stored_verification_code==null) {
			throw new CustomException(AuthError.NOT_FOUND_VERIFICATION_CODE);
		}

		userCheckUtil.checkVerificationcodeAndStoredVerificationcode(stored_verification_code,verification_code);
		userCheckUtil.isUserIdNotDuplicate(userMapper.readUserInfoByUserId(user_id));
		userCheckUtil.isUserPhoneNotDuplicate(userMapper.readUserInfoByUserPhone(user_phone));
		userCheckUtil.isQuestionExistent(Integer.parseInt(question_id));
		
		String user_salt = SHA.getSalt();
		param.put("user_pw", SHA.DSHA512(user_pw, user_salt));
		param.put("user_salt", user_salt);
		param.put("question_answer", SHA.DSHA512(question_answer.replaceAll(" ", ""), user_salt));
		
		userMapper.createUser(param);
		userMapper.createUserTime(param);
		
		redisUtil.delete(PREFIX_VERIFICATION_CODE+user_phone);
		return;
	}

	@Override
	public void updateMyUserInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String user_phone = param.get("user_phone");
		
		param.put("user_id", user_id);
		
		boolean changeFlag = false;
		
		String verification_code = param.get("verification_code");
		String new_user_gender = param.get("new_user_gender");
		String new_user_pw = param.get("new_user_pw");
		String new_user_pw_check = param.get("new_user_pw_check");
		String new_user_name = param.get("new_user_name");
		String new_user_phone = param.get("new_user_phone");
		String new_question_id = param.get("new_question_id");
		String new_question_answer = param.get("new_question_answer");
		String question_answer = param.get("question_answer");
		
		UserEntity userEntity = userCheckUtil.isUserExistent(user_id);
		String user_salt = userEntity.getUser_salt();
		String user_refreshtoken = userEntity.getUser_refreshtoken();
		
		userCheckUtil.checkUserPhoneRegex(user_phone);
		userCheckUtil.checkUserPhones(user_phone,userEntity.getUser_phone());
		userCheckUtil.checkUserVerificationcodeRegex(verification_code);
		
		String stored_verification_code = redisUtil.getData(PREFIX_VERIFICATION_CODE+user_phone);
		
		if(stored_verification_code==null) {
			throw new CustomException(AuthError.NOT_FOUND_VERIFICATION_CODE);
		}
		
		userCheckUtil.checkVerificationcodeAndStoredVerificationcode(stored_verification_code, stored_verification_code);
		userCheckUtil.checkUserPhones(user_phone, userEntity.getUser_phone());
		
		if(new_user_pw!=null&&new_user_pw_check!=null) {
			userCheckUtil.checkUserPwRegex(new_user_pw);
			userCheckUtil.checkUserPwRegex(new_user_pw_check);
			userCheckUtil.checkUserPwAndPwCheck(new_user_pw, new_user_pw_check);
			changeFlag = true;
		}
		
		if(new_user_name!=null) {
			userCheckUtil.checkUserNameRegex(new_user_name);
			param.put("user_name", new_user_name);
			changeFlag = true;
		}
		
		if(new_user_gender!=null) {
			userCheckUtil.checkUserGender(new_user_gender);
			param.put("user_gender", new_user_gender);
			changeFlag = true;
		}
		
		if(new_user_phone!=null) {
			String new_verification_code = param.get("new_verification_code");
			
			userCheckUtil.checkUserVerificationcodeRegex(new_verification_code);
			userCheckUtil.checkUserPhoneRegex(new_user_phone);
			userCheckUtil.isUserPhoneNotDuplicate(userMapper.readUserInfoByUserPhone(new_user_phone));

			String new_stored_verification_code = redisUtil.getData(PREFIX_VERIFICATION_CODE+new_user_phone);
			
			if(new_stored_verification_code==null) {
				throw new CustomException(AuthError.NOT_FOUND_VERIFICATION_CODE);
			}
			
			userCheckUtil.checkVerificationcodeAndStoredVerificationcode(new_verification_code, new_stored_verification_code);
			
			param.put("new_user_phone", new_user_phone);
			changeFlag = true;
		}
		
		if(question_answer!=null&&new_user_pw!=null&&new_question_id!=null&&new_question_answer!=null) {
			userCheckUtil.checkQuestionIdRegex(new_question_id);
			userCheckUtil.checkQuestionAnswerBytes(new_question_answer);
			userCheckUtil.checkQuestionAnswerBytes(question_answer);
			userCheckUtil.checkUserQuestionAnswerAndOldQuestionAnswer(SHA.DSHA512(question_answer.replaceAll(" ", ""), user_salt), userEntity.getQuestion_answer());
			userCheckUtil.isQuestionExistent(Integer.parseInt(new_question_id));
			
			String new_user_salt = SHA.getSalt();
			param.put("new_question_id", new_question_id);
			param.put("new_user_salt", new_user_salt);
			param.put("new_user_pw", SHA.DSHA512(new_user_pw, new_user_salt));
			param.put("new_question_answer", SHA.DSHA512(new_question_answer.replaceAll(" ", ""), new_user_salt));	
			
			userMapper.updateUserPwChangeTime(param);
			changeFlag = true;
		}else if(new_user_pw==null&&new_question_id==null&&new_question_answer==null) {
			
		}else {
			throw new CustomException(UserError.USER_PW_AND_QUESTION_ID_AND_QUESTION_ANSWER_ARE_NEEDED_AT_THE_SAME_TIME);
		}

		if(changeFlag) {
			userMapper.updateMyUserInfo(param);
		}else {
			throw new CustomException(UserError.CAN_NOT_UPDATE_USER_WITHOUT_ANY_CHANGE);
		}
		
		redisUtil.setData(user_accesstoken, "removed", jwtTokenProvider.getRemainingTime(user_accesstoken));
		redisUtil.setData(user_refreshtoken, "removed", jwtTokenProvider.getRemainingTime(user_refreshtoken));
		
		if(new_user_phone!=null) {
			redisUtil.delete(PREFIX_VERIFICATION_CODE+new_user_phone);
		}
		
		redisUtil.delete(PREFIX_VERIFICATION_CODE+user_phone);
		return;
	}

	@Override
	public void createBlockingInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String target_user_id = param.get("target_user_id");
		
		param.put("source_user_id", source_user_id);
		
		userCheckUtil.checkUserIdRegex(target_user_id);
		userCheckUtil.isSourceUserIdAndTargetUserIdNotSame(source_user_id, target_user_id);
		userCheckUtil.isUserExistent(target_user_id);
		userCheckUtil.checkNumberOfBlockingInfo(source_user_id);
		userCheckUtil.isNotBlocked(source_user_id, target_user_id);

		userMapper.createBlockingInfo(param);
		
		return;
	}

	@Override
	public void deleteBlockingInfo(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String target_user_id = param.get("target_user_id");
		
		param.put("source_user_id", source_user_id);
		userCheckUtil.checkUserIdRegex(target_user_id);
		userCheckUtil.isSourceUserIdAndTargetUserIdNotSame(source_user_id, target_user_id);
		userCheckUtil.isUserRegistered(target_user_id);
		userCheckUtil.isBlocked(source_user_id, target_user_id);
		
		userMapper.deleteBlockingInfoByBothUserId(param);
		
		return;
	}

	@Override
	public List<BlockingDTO> readBlockingInfo(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String source_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		List<BlockingDTO> blockings = new LinkedList<BlockingDTO>();
		
		for(BlockingEntity blockingEntity : userMapper.readBlockingInfoBySourceUserId(source_user_id)) {
			blockings.add(new BlockingDTO(blockingEntity));
		}
		
		return blockings;
	}

	@Override
	public QuestionDTO readQuestion(HttpServletRequest request, HashMap<String,String> param) {
		String user_id = param.get("user_id");
		
		userCheckUtil.checkUserIdRegex(user_id);
		UserEntity userEntity = userCheckUtil.isUserExistent(user_id);
		
		QuestionEntity questionEntity = new QuestionEntity();
		questionEntity.setQuestion_id(userEntity.getQuestion_id());
		questionEntity.setQuestion_content(userEntity.getQuestion_content());
		
		return new QuestionDTO(questionEntity);
	}

	@Override
	public List<QuestionDTO> readQuestions() {
		List<QuestionDTO> questions = new LinkedList<QuestionDTO>();
		
		for(QuestionEntity questionEntity : userMapper.readQuestions()) {
			questions.add(new QuestionDTO(questionEntity));
		}
		
		return questions;
	}

	@Override
	public void updateUserPw(HttpServletRequest request, HashMap<String,String> param) {
		String user_id = param.get("user_id");
		String user_phone = param.get("user_phone");
		String question_answer = param.get("question_answer");
		String new_question_id = param.get("new_question_id");
		String new_question_answer = param.get("new_question_answer");
		String new_user_pw = param.get("new_user_pw");
		String new_user_pw_check = param.get("new_user_pw_check");
		String new_user_salt = SHA.getSalt();
		String verification_code = param.get("verification_code");
		String stored_verification_code = redisUtil.getData(PREFIX_VERIFICATION_CODE+user_phone);
		
		userCheckUtil.checkUserIdRegex(user_id);
		userCheckUtil.checkUserPhoneRegex(user_phone);
		userCheckUtil.checkQuestionIdRegex(new_question_id);
		userCheckUtil.checkQuestionAnswerBytes(new_question_answer);
		userCheckUtil.checkUserPwRegex(new_user_pw);
		userCheckUtil.checkUserPwRegex(new_user_pw_check);
		userCheckUtil.checkUserPwAndPwCheck(new_user_pw, new_user_pw_check);
		userCheckUtil.checkUserAuthcodeRegex(verification_code);
		userCheckUtil.isQuestionExistent(Integer.parseInt(new_question_id));
		
		if(stored_verification_code==null) {
			throw new CustomException(AuthError.NOT_FOUND_VERIFICATION_CODE);
		}

		userCheckUtil.checkVerificationcodeAndStoredVerificationcode(stored_verification_code,verification_code);
		
		UserEntity userEntity = userCheckUtil.isUserExistent(user_id);
		userCheckUtil.checkUserPhones(user_phone,userEntity.getUser_phone());
		userCheckUtil.checkUserQuestionAnswerAndOldQuestionAnswer(SHA.DSHA512(question_answer.replaceAll(" ", ""), userEntity.getUser_salt()), userEntity.getQuestion_answer());
		
		param.put("user_id", user_id);
		param.put("new_user_salt", new_user_salt);
		param.put("new_user_pw", SHA.DSHA512(new_user_pw.replaceAll(" ", ""), new_user_salt));
		param.put("new_question_answer", SHA.DSHA512(new_question_answer.replaceAll(" ", ""), new_user_salt));
		
		userMapper.updateUserPw(param);
		userMapper.updateUserPwChangeTime(param);
		userMapper.updateUserTokensToNull(param);
		
		String user_accesstoken = userEntity.getUser_accesstoken();
		String user_refreshtoken = userEntity.getUser_refreshtoken();
		
		redisUtil.setData(user_accesstoken, "removed", jwtTokenProvider.getRemainingTime(user_accesstoken));
		redisUtil.setData(user_refreshtoken, "removed", jwtTokenProvider.getRemainingTime(user_refreshtoken));
		redisUtil.delete(PREFIX_VERIFICATION_CODE+user_phone);
	}

	@Override
	public UserDTO readMyUserInfo(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		UserEntity userEntity = userMapper.readUserInfoByUserId(user_id);
		
		return new UserDTO(userEntity);
	}

	@Override
	public void deleteMyUserInfo(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		
		UserEntity userEntity = userCheckUtil.isUserExistent(user_id);
		
		user_accesstoken = userEntity.getUser_accesstoken();
		String user_refreshtoken = userEntity.getUser_refreshtoken();
		
		userMapper.deleteUser(param);
		userMapper.updateUserWithdrawTime(param);
		
		redisUtil.setData(user_accesstoken, "removed", jwtTokenProvider.getRemainingTime(user_accesstoken));
		redisUtil.setData(user_refreshtoken, "removed", jwtTokenProvider.getRemainingTime(user_refreshtoken));
	}

	@Override
	public void checkDuplicateUserId(HashMap param) {
		String user_id = (String) param.get("user_id");
		userCheckUtil.checkUserIdRegex(user_id);
		userCheckUtil.isUserIdNotDuplicate(user_id);
	}

	@Override
	public void checkDuplicateUserPhone(HashMap param) {
		String user_phone = (String) param.get("user_phone");
		userCheckUtil.checkUserPhoneRegex(user_phone);
		userCheckUtil.isUserPhoneNotDuplicate(user_phone);
	}
}