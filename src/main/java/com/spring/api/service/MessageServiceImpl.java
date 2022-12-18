package com.spring.api.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.api.dto.DetailedMessageDTO;
import com.spring.api.dto.MessageDTO;
import com.spring.api.entity.MessageEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.MessageMapper;
import com.spring.api.util.MessageCheckUtil;
import com.spring.api.util.UserCheckUtil;

@Service("messageService")
@Transactional(rollbackFor= {Exception.class})
public class MessageServiceImpl implements MessageService{
	private final MessageMapper messageMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserCheckUtil userCheckUtil;
	private final MessageCheckUtil messageCheckUtil;
	
	@Autowired
	MessageServiceImpl(MessageMapper messageMapper, JwtTokenProvider jwtTokenProvider, UserCheckUtil userCheckUtil, MessageCheckUtil MessageCheckUtil){
		this.messageMapper = messageMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userCheckUtil = userCheckUtil;
		this.messageCheckUtil = MessageCheckUtil;
	}
	
	@Override
	public void createMessage(HttpServletRequest request, @RequestBody HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String message_sender_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("message_sender_user_id", message_sender_user_id);
		
		String message_type_id = param.get("message_type_id");
		String message_title = param.get("message_title");
		String message_content = param.get("message_content");
		String message_receiver_user_id = param.get("message_receiver_user_id");
		
		messageCheckUtil.checkMessageTypeIdRegex(message_type_id);
		messageCheckUtil.checkMessageTitleBytes(message_title);
		messageCheckUtil.checkMessageContentBytes(message_content);
		userCheckUtil.checkUserIdRegex(message_receiver_user_id);
		userCheckUtil.isUserExistent(message_receiver_user_id);
		messageCheckUtil.isMessageTypeExistent(Integer.parseInt(message_type_id));
		messageCheckUtil.checkUserMessageTime(messageMapper.readUserMessageTime(param));		
		
		param.put("message_id", messageMapper.readNewMessageId()+"");
		
		messageMapper.createMessage(param);
		messageMapper.createMessageSender(param);
		messageMapper.createMessageReceiver(param);
		messageMapper.updateUserMessageTime(param);

		return;
	}

	@Override
	public void deleteMessage(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String message_id = param.get("message_id");
		param.put("user_id", user_id);
		
		messageCheckUtil.checkMessageIdRegex(message_id);
		messageCheckUtil.isMessageExistent(param);
		
		messageMapper.deleteMessageBySenderId(param);
		messageMapper.deleteMessageByReceiverId(param);
	}

	@Override
	public DetailedMessageDTO readMessage(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String message_id = param.get("message_id");
		param.put("user_id", user_id);
		
		MessageEntity messageEntity = messageCheckUtil.isMessageExistent(param);
		
		return new DetailedMessageDTO(messageEntity);
	}

	@Override
	public List<MessageDTO> readBulkMessage(HttpServletRequest request, HashMap<String, String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String message_box = messageCheckUtil.checkMessageBox(param.get("message_box"));
		String flag = messageCheckUtil.checkFlag(param.get("flag"), message_box);
		String search = messageCheckUtil.checkSearch(flag,param.get("search"));
		String order = messageCheckUtil.checkOrderRegex(param.get("order"));
		
		messageCheckUtil.checkSearch(flag,search);
		
		param.put("message_box", message_box);
		
		if(message_box.equalsIgnoreCase("message_receivers")) {
			param.put("owner_user_id", "message_receiver_user_id");
			param.put("owner_user_name", "message_receiver_user_name");
			param.put("owner_message_status", "message_receiver_status");
			param.put("owner_message_delete_time", "message_receiver_delete_time");
			
			param.put("other_user_id", "message_sender_user_id");
			param.put("other_user_name", "message_sender_user_name");
			param.put("other_message_box", "message_senders");
			param.put("other_message_delete_time", "message_sender_delete_time");
		}else {
			param.put("owner_user_id", "message_sender_user_id");
			param.put("owner_user_name", "message_sender_user_name");
			param.put("owner_message_status", "message_sender_status");
			param.put("owner_message_delete_time", "message_sender_delete_time");
			
			param.put("other_user_id", "message_receiver_user_id");
			param.put("other_user_name", "message_receiver_user_name");
			param.put("other_message_box", "message_receivers");
			param.put("other_message_delete_time", "message_receiver_delete_time");
		}
		
		param.put("flag", flag);
		param.put("search", search);
		param.put("order", order);
		
		int limit = messageCheckUtil.checkLimitRegex(param.get("limit"));	
		int page = messageCheckUtil.checkPageRegex(param.get("page"));

		param.put("offset", (page*limit)+"");
		param.put("limit", limit+"");
		param.put("page", page+"");
		
		List<MessageDTO> messages = new LinkedList<MessageDTO>();
		
		for(MessageEntity messageEntity : messageMapper.readMessages(param)) {
			messages.add(new MessageDTO(messageEntity));
		}
		
		return messages;
	}
}