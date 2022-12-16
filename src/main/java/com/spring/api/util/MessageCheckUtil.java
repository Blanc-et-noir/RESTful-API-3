package com.spring.api.util;

import java.sql.Timestamp;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spring.api.code.MessageError;
import com.spring.api.entity.MessageEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.mapper.MessageMapper;

import io.jsonwebtoken.lang.Strings;

@Component
public class MessageCheckUtil {
	private MessageMapper messageMapper;
    private RegexUtil regexUtil;
	private final long MESSAGE_FREQUENCY;
	
	@Autowired
	MessageCheckUtil(
		MessageMapper messageMapper,
		RegexUtil regexUtil,
		@Value("${message.frequency}") long MESSAGE_FREQUENCY
	){
		this.messageMapper = messageMapper;
		this.regexUtil = regexUtil;
		this.MESSAGE_FREQUENCY = MESSAGE_FREQUENCY;
	}
	
	public void isMessageTypeExistent(int message_type_id) {
		if(messageMapper.readMessageTypeByMessageTypeId(message_type_id)==null) {
			throw new CustomException(MessageError.NOT_FOUND_MESSAGE_TYPE);
		}
	}	
	
	public void checkMessageTypeIdRegex(String message_type_id) {
		try {
			Integer.parseInt(message_type_id);
		}catch(Exception e) {
			throw new CustomException(MessageError.MESSAGE_TYPE_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public int checkPageRegex(String page) {
		int num = 0;
		
		if(page==null) {
			return 0;
		}
		
		try {
			 num = Integer.parseInt(page);
		}catch(Exception e) {
			throw new CustomException(MessageError.PAGE_NOT_MATCHED_TO_REGEX);
		}
		
		return num;
	}

	public int checkLimitRegex(String limit) {
		int val = 0;
		
		if(limit==null) {System.out.println(regexUtil.getMIN_LIMIT()+" "+regexUtil.getMAX_LIMIT());
			return regexUtil.getMIN_LIMIT();
		}
		
		try {
			val = Integer.parseInt(limit);
		}catch(Exception e) {			
			throw new CustomException(MessageError.LIMIT_NOT_MATCHED_TO_REGEX);
		}
		
		if(!(val>=regexUtil.getMIN_LIMIT()&&val<=regexUtil.getMAX_LIMIT())) {
			throw new CustomException(MessageError.LIMIT_OUT_OF_RANGE);
		}
		
		return val;
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
			}else if(!regexUtil.checkBytes(search, regexUtil.getSEARCH_MAX_BYTES())) {
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

	public void checkUserMessageTime(Timestamp userMessageTime) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		if(userMessageTime != null && (now.getTime()-userMessageTime.getTime())/1000 <= MESSAGE_FREQUENCY) {
			throw new CustomException(MessageError.TOO_FREQUENT_TO_SEND_MESSAGE);
		}
	}
}