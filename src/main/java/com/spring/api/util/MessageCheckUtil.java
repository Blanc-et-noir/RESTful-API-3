package com.spring.api.util;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.spring.api.code.MessageError;
import com.spring.api.code.UserError;
import com.spring.api.entity.MessageEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.MessageMapper;
import com.spring.api.mapper.UserMapper;

import io.jsonwebtoken.lang.Strings;

@Component
public class MessageCheckUtil {
	private JwtTokenProvider jwtTokenProvider;
	private UserMapper userMapper;
	private MessageMapper messageMapper;
    private RedisTemplate<String, String> redisTemplate;
    private RegexUtil regexUtil;
    
	private final int FOLLOWING_LIMIT = 5;
	private final int BLOCKING_LIMIT = 5;
	private final int MESSAGE_RECEIVER_USER_ID_LIMIT = 10;
	private final int SEARCH_LIMIT_BYTES = 120;
	private final int MAX_LIMIT = 50;
	private final int MIN_LIMIT = 10;
	
	@Autowired
	MessageCheckUtil(UserMapper userMapper,MessageMapper messageMapper, JwtTokenProvider jwtTokenProvider, RedisTemplate redisTemplate, RegexUtil regexUtil){
		this.userMapper = userMapper;
		this.messageMapper = messageMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisTemplate = redisTemplate;
		this.regexUtil = regexUtil;
	}
	
	public void isMessageTypeExistent(int message_type_id) {
		if(messageMapper.readMessageTypeByMessageTypeId(message_type_id)==null) {
			throw new CustomException(MessageError.NOT_FOUND_MESSAGE_TYPE);
		}
	}
	
	
	public void areUsersExistent(List<String> message_receiver_user_ids) {
		if(messageMapper.readUsersByUserIds(message_receiver_user_ids).size()<message_receiver_user_ids.size()) {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
	}
	
	
	public void checkMessageTypeIdRegex(String message_type_id) {
		try {
			Integer.parseInt(message_type_id);
		}catch(Exception e) {
			throw new CustomException(MessageError.MESSAGE_TYPE_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkUserIdsRegex(List<String> user_ids) {
		for(String user_id : user_ids) {
			if(!regexUtil.checkRegex(user_id, regexUtil.getUSER_ID_REGEX())) {
				throw new CustomException(UserError.USER_ID_NOT_MATCHED_TO_REGEX);
			}
		}
	}
	
	public int checkPageRegex(String page) {
		int num = 0;

		try {
			num = Integer.parseInt(page);
		}catch(Exception e) {
			throw new CustomException(MessageError.PAGE_NOT_MATCHED_TO_REGEX);
		}
		
		return num;
	}
	
	public int checkLimitRegex(String limit) {
		int val = 0;
		
		try {
			val = Integer.parseInt(limit);
		}catch(Exception e) {			
			throw new CustomException(MessageError.LIMIT_NOT_MATCHED_TO_REGEX);
		}
		
		if(!(val>=MIN_LIMIT&&val<=MAX_LIMIT)) {
			throw new CustomException(MessageError.LIMIT_OUT_OF_RANGE);
		}
		
		return val;
	}
	
	public void areTheNumberOfUserIdsProper(List<String> message_receiver_user_ids) {
		if(!(!message_receiver_user_ids.isEmpty()&&message_receiver_user_ids.size()>=1&&message_receiver_user_ids.size()<=MESSAGE_RECEIVER_USER_ID_LIMIT)) {
			throw new CustomException(MessageError.THE_NUMBER_OF_USER_IDS_ARE_NOT_PROPER);
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
	
	public String checkMessageBox(String message_box) {
		if(message_box==null || !Strings.hasText(message_box) || message_box.equalsIgnoreCase("R")) {
			return "message_receivers";
		}else if(message_box.equalsIgnoreCase("S")) {
			return "message_senders";
		}else {
			throw new CustomException(MessageError.MESSAGE_BOX_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public String checkFlag(String flag, String message_box) {
		if(flag==null || !Strings.hasText(flag) || flag.equalsIgnoreCase("T")) {
			return "message_title";
		}else if(flag.equalsIgnoreCase("C")){
			return "message_content";
		}else if(flag.equalsIgnoreCase("U")){
			if(message_box.equalsIgnoreCase("message_receivers")) {
				return "message_sender_user_id";
			}else {
				return "message_receiver_user_id";
			}
		}else {
			throw new CustomException(MessageError.FLAG_NOT_MATCHED_TO_REGEX);
		}
	}

	public String checkSearch(String flag, String search) {
		if(flag.equalsIgnoreCase("message_title")||flag.equalsIgnoreCase("message_content")) {
			if(search == null || !Strings.hasText(search)) {
				return "";
			}else if(!regexUtil.checkBytes(search, SEARCH_LIMIT_BYTES)) {
				throw new CustomException(MessageError.SEARCH_EXCEED_MAX_BYTES);
			}
			return search;
		}else{
			if(!regexUtil.checkRegex(search,regexUtil.getUSER_ID_REGEX())) {
				throw new CustomException(MessageError.USER_ID_NOT_MATCHED_TO_REGEX);
			}
			return search;
		}		
	}

	public String checkOrderRegex(String order) {
		if(order == null || !Strings.hasText(order) || order.equalsIgnoreCase("D")) {
			return "DESC";
		}else if(order.equalsIgnoreCase("A")) {
			return "ASC";
		}else {
			throw new CustomException(MessageError.ORDER_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public void checkMessageIdRegex(String message_id) {
		try {
			Integer.parseInt(message_id);
		}catch(Exception e) {
			throw new CustomException(MessageError.MESSAGE_ID_NOT_MATCHED_TO_REGEX);
		}
	}

	public MessageEntity isMessageExistent(HashMap param) {
		MessageEntity messageEntity = messageMapper.readMessageByMessageIdAndUserId(param);
		
		if(messageEntity==null) {
			throw new CustomException(MessageError.NOT_FOUND_MESSAGE);
		}else {
			return messageEntity;
		}
	}
}
