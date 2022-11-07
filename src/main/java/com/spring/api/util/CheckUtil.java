package com.spring.api.util;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.spring.api.code.AuthError;
import com.spring.api.code.MessageError;
import com.spring.api.code.UserError;
import com.spring.api.entity.MessageEntity;
import com.spring.api.entity.QuestionEntity;
import com.spring.api.entity.UserEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.MessageMapper;
import com.spring.api.mapper.UserMapper;

@Component
public class CheckUtil {
	private JwtTokenProvider jwtTokenProvider;
	private UserMapper userMapper;
	private MessageMapper messageMapper;
    private RedisTemplate<String, String> redisTemplate;
    private RegexUtil regexUtil;
    
	private final int FOLLOWING_LIMIT = 5;
	private final int BLOCKING_LIMIT = 5;

	@Autowired
	CheckUtil(UserMapper userMapper,MessageMapper messageMapper, JwtTokenProvider jwtTokenProvider, RedisTemplate redisTemplate, RegexUtil regexUtil){
		this.userMapper = userMapper;
		this.messageMapper = messageMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisTemplate = redisTemplate;
		this.regexUtil = regexUtil;
	}
	
	public void checkAccessToken(String old_user_accesstoken, String user_accesstoken) {
		if(!StringUtils.hasText(user_accesstoken)) {
			throw new CustomException(AuthError.NOT_FOUND_USER_ACCESSTOKEN);
		}else if(!jwtTokenProvider.validateToken(user_accesstoken)) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}else if(!jwtTokenProvider.getTokenType(user_accesstoken).equals("user_accesstoken")) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}else if(redisTemplate.opsForValue().get(user_accesstoken)!=null){
			throw new CustomException(AuthError.IS_LOGGED_OUT_ACCESSTOKEN);
		}else if(old_user_accesstoken==null) {
			throw new CustomException(AuthError.NOT_FOUND_STORED_USER_ACCESSTOKEN);
		}else if(!old_user_accesstoken.equals(user_accesstoken)) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}
	}
	
	public void checkRefreshToken(String old_user_refreshtoken, String user_refreshtoken) {
		if(!StringUtils.hasText(user_refreshtoken)) {
			throw new CustomException(AuthError.NOT_FOUND_USER_REFRESHTOKEN);
		}else if(!jwtTokenProvider.validateToken(user_refreshtoken)) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}else if(!jwtTokenProvider.getTokenType(user_refreshtoken).equals("user_refreshtoken")) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}else if(redisTemplate.opsForValue().get(user_refreshtoken)!=null){
			throw new CustomException(AuthError.IS_LOGGED_OUT_REFRESHTOKEN);
		}else if(old_user_refreshtoken==null) {
			throw new CustomException(AuthError.NOT_FOUND_STORED_USER_REFRESHTOKEN);
		}else if(!old_user_refreshtoken.equals(user_refreshtoken)) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}
	}
	
	public void checkUserIdRegex(String user_id) {
		if(!regexUtil.checkRegex(user_id, regexUtil.getUSER_ID_REGEX())) {
			throw new CustomException(UserError.USER_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserIdsRegex(List<String> user_ids) {
		for(String user_id : user_ids) {
			if(!regexUtil.checkRegex(user_id, regexUtil.getUSER_ID_REGEX())) {
				throw new CustomException(UserError.USER_ID_NOT_MATCHED_TO_REGEX);
			}
		}
	}
	
	public void areUserIdsNotEmpty(List<String> user_ids) {
		if(user_ids.isEmpty()||user_ids.size()<=0) {
			throw new CustomException(MessageError.ARE_USER_IDS_EMPTY);
		}
	}
	
	public void checkUserPwRegex(String user_pw) {
		if(!regexUtil.checkRegex(user_pw, regexUtil.getUSER_PW_REGEX())) {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserPwAndPwCheck(String user_pw, String user_pw_check) {		
		if(!user_pw.equals(user_pw_check)) {
			throw new CustomException(UserError.USER_PW_NOT_EQUAL_TO_USER_PW_CHECK);
		}
	}
	
	public void checkUserGender(String user_gender) {
		if(!regexUtil.checkRegex(user_gender, regexUtil.getUSER_GENDER_REGEX())) {
			throw new CustomException(UserError.USER_GENDER_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserQuestionAnswerAndOldQuestionAnswer(String question_answer, String old_question_answer) {		
		if(!question_answer.equals(old_question_answer)) {
			throw new CustomException(UserError.QUESTION_ANSWER_NOT_EQUAL_TO_OLD_QUESTION_ANSWER);
		}
	}
	
	public void checkUserNameRegex(String user_name) {
		if(!regexUtil.checkRegex(user_name, regexUtil.getUSER_NAME_REGEX())) {
			throw new CustomException(UserError.USER_NAME_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserPhoneRegex(String user_phone) {
		if(!regexUtil.checkRegex(user_phone, regexUtil.getUSER_PHONE_REGEX())) {
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
	
	public void checkMessageTypeIdRegex(String message_type_id) {
		try {
			Integer.parseInt(message_type_id);
		}catch(Exception e) {
			throw new CustomException(MessageError.MESSAGE_TYPE_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkQuestionAnswerBytes(String question_answer) {
		if(!regexUtil.checkBytes(question_answer, regexUtil.getQUESTION_ANSWER_MAX_BYTES())) {
			throw new CustomException(UserError.QUESTION_ANSWER_EXCEED_MAX_BYTES);
		}
	}
	
	public void checkMessageTitleBytes(String message_title) {
		if(!regexUtil.checkBytes(message_title, regexUtil.getMESSAGE_TITLE_MAX_BYTES())) {
			throw new CustomException(MessageError.MESSAGE_TITLE_EXCEED_MAX_BYTES);
		}
	}
	
	public void checkMessageContentBytes(String message_content) {
		if(!regexUtil.checkBytes(message_content, regexUtil.getMESSAGE_CONTENT_MAX_BYTES())) {
			throw new CustomException(MessageError.MESSAGE_CONTENT_EXCEED_MAX_BYTES);
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
	
	public void isMessageTypeExistent(int message_type_id) {
		if(messageMapper.readMessageTypeByMessageTypeId(message_type_id)==null) {
			throw new CustomException(MessageError.NOT_FOUND_MESSAGE_TYPE);
		}
	}
	
	public void isSourceUserIdAndTargetUserIdNotSame(String source_user_id, String target_user_id) {
		if(source_user_id.equals(target_user_id)) {
			throw new CustomException(UserError.SOURCE_USER_ID_EQUAL_TO_TARGET_USER_ID);
		}
	}

	public void checkNumberOfFollowingInfo(String source_user_id) {
		if(userMapper.readFollowingInfoBySourceUserId(source_user_id).size()>=FOLLOWING_LIMIT) {
			throw new CustomException(UserError.NUMBER_OF_FOLLOWING_INFO_EXCEED_LIMIT);
		}
	}

	public void checkNumberOfBlockingInfo(String source_user_id) {
		if(userMapper.readBlockingInfoBySourceUserId(source_user_id).size()>=BLOCKING_LIMIT) {
			throw new CustomException(UserError.NUMBER_OF_BLOCKING_INFO_EXCEED_LIMIT);
		}
	}
	
	public void isNotBlocked(String source_user_id, String target_user_id) {
		HashMap param = new HashMap();
		param.put("source_user_id", source_user_id);
		param.put("target_user_id", target_user_id);
		
		if(userMapper.readBlockingInfoByBothUserId(param) != null) {
			throw new CustomException(UserError.DUPLICATE_BLOCKING_INFO);
		}
	}
	
	public void isNotFollowed(String source_user_id, String target_user_id) {
		HashMap param = new HashMap();
		param.put("source_user_id", source_user_id);
		param.put("target_user_id", target_user_id);
		
		if(userMapper.readFollowingInfoByBothUserId(param) != null) {
			throw new CustomException(UserError.DUPLICATE_FOLLOWING_INFO);
		}
	}
	
	public void isBlocked(String source_user_id, String target_user_id) {
		HashMap param = new HashMap();
		param.put("source_user_id", source_user_id);
		param.put("target_user_id", target_user_id);
		
		if(userMapper.readBlockingInfoByBothUserId(param) == null) {
			throw new CustomException(UserError.IS_NOT_BLOCKED_USER_ID);
		}
	}
	
	public void isFollowed(String source_user_id, String target_user_id) {
		HashMap param = new HashMap();
		param.put("source_user_id", source_user_id);
		param.put("target_user_id", target_user_id);
		
		if(userMapper.readFollowingInfoByBothUserId(param) == null) {
			throw new CustomException(UserError.IS_NOT_FOLLOWED_USER_ID);
		}
	}
	
	public void areUsersExistent(List<String> user_ids) {
		if(messageMapper.readUsersByUserIds(user_ids).size()<user_ids.size()) {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
	}
}