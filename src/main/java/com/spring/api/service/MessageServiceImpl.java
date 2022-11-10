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
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.MessageMapper;
import com.spring.api.util.MessageCheckUtil;

@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService{
	private final MessageMapper messageMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final MessageCheckUtil messageCheckUtil;
	
	@Autowired
	MessageServiceImpl(MessageMapper messageMapper, JwtTokenProvider jwtTokenProvider, MessageCheckUtil MessageCheckUtil){
		this.messageMapper = messageMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.messageCheckUtil = MessageCheckUtil;
	}
	
	@Override
	public void createBulkMessage(HttpServletRequest request, @RequestBody HashMap<String,Object> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String message_sender_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("message_sender_user_id", message_sender_user_id);
		
		String message_type_id = (String) param.get("message_type_id");
		String message_title = (String) param.get("message_title");
		String message_content = (String) param.get("message_content");
		
		messageCheckUtil.checkMessageTypeIdRegex(message_type_id);
		messageCheckUtil.checkMessageTitleBytes(message_title);
		messageCheckUtil.checkMessageContentBytes(message_content);
		
		List<String> message_receiver_user_ids = (List<String>) param.get("message_receiver_user_ids");
		
		messageCheckUtil.areTheNumberOfUserIdsProper(message_receiver_user_ids);
		messageCheckUtil.checkUserIdsRegex(message_receiver_user_ids);
		messageCheckUtil.areUsersExistent(message_receiver_user_ids);
		messageCheckUtil.isMessageTypeExistent(Integer.parseInt(message_type_id));
		
		int message_id = messageMapper.readNewMessageId();
		param.put("message_id", message_id);
		param.put("message_receiver_user_ids", message_receiver_user_ids);
		
		message_receiver_user_ids = messageMapper.readUsersByNotBlockedUserIds(param);
		param.put("message_receiver_user_ids", message_receiver_user_ids);

		if(message_receiver_user_ids.size()!=0) {
			messageMapper.createBulkMessage(param);
			messageMapper.createMessageSenderInfo(param);
			messageMapper.createMessageReceiverInfo(param);
		}
	}

	@Override
	public void deleteMessage(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String message_id = param.get("message_id");
		param.put("user_id", user_id);
		
		messageCheckUtil.checkMessageIdRegex(message_id);
		MessageEntity messageEntity = messageCheckUtil.isMessageExistent(param);
		
		messageMapper.deleteMessageBySenderId(param);
		messageMapper.deleteMessageByReceiverId(param);
	}

	@Override
	public DetailedMessageDTO readMessage(HttpServletRequest request, HashMap<String, String> param, String message_id) {
		// TODO Auto-generated method stub
		return null;
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
		String page = param.get("page");
		
		messageCheckUtil.checkSearch(flag,search);
		
		param.put("message_box", message_box);
		
		if(message_box.equalsIgnoreCase("message_receivers")) {
			param.put("owner_user_id", "message_receiver_user_id");
			param.put("owner_message_status", "message_receiver_status");
			param.put("other_message_box", "message_senders");
		}else {
			param.put("owner_user_id", "message_sender_user_id");
			param.put("owner_message_status", "message_sender_status");
			param.put("other_message_box", "message_receivers");
		}
		
		param.put("flag", flag);
		param.put("search", search);
		param.put("order", order);
		
		final int MAX_PAGE = messageMapper.countMessages(param);
		
		int limit = messageCheckUtil.checkLimitRegex(param.get("limit"));	
		messageCheckUtil.checkPageRegex(MAX_PAGE, limit, page);
		
		param.put("offset", Integer.parseInt(page)*limit+"");
		
		List<MessageDTO> messages = new LinkedList<MessageDTO>();
		
		for(MessageEntity messageEntity : messageMapper.readBulkMessage(param)) {
			messages.add(new MessageDTO(messageEntity));
		}
		
		return messages;
	}
}