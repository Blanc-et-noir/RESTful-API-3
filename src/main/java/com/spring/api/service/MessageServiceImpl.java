package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.api.dto.MessageDTO;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.MessageMapper;
import com.spring.api.util.CheckUtil;
import com.spring.api.util.RedisUtil;

@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService{
	private final MessageMapper messageMapper;
	private final JwtTokenProvider jwtTokenProvider;
	private final CheckUtil checkUtil;
    private final RedisUtil redisUtil;
	
	@Autowired
	MessageServiceImpl(MessageMapper messageMapper, JwtTokenProvider jwtTokenProvider, CheckUtil checkUtil, RedisUtil redisUtil){
		this.messageMapper = messageMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.checkUtil = checkUtil;
		this.redisUtil = redisUtil;
	}
	
	@Override
	public void createBulkMessage(HttpServletRequest request, @RequestBody HashMap<String,Object> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String message_sender_user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("message_sender_user_id", message_sender_user_id);
		
		String message_type_id = (String) param.get("message_type_id");
		String message_title = (String) param.get("message_title");
		String message_content = (String) param.get("message_content");
		
		checkUtil.checkMessageTypeIdRegex(message_type_id);
		checkUtil.checkMessageTitleBytes(message_title);
		checkUtil.checkMessageContentBytes(message_content);
		
		List<String> message_receiver_user_ids = (List<String>) param.get("message_receiver_user_ids");
		
		checkUtil.areUserIdsNotEmpty(message_receiver_user_ids);
		checkUtil.checkUserIdsRegex(message_receiver_user_ids);
		checkUtil.areUsersExistent(message_receiver_user_ids);
		checkUtil.isMessageTypeExistent(Integer.parseInt(message_type_id));
		
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
	public void deleteMessage(HttpServletRequest request, HashMap<String, String> param, String message_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteBulkMessage(HttpServletRequest request, HashMap<String, String> param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageDTO readMessage(HttpServletRequest request, HashMap<String, String> param, String message_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MessageDTO> readBulkMessage(HttpServletRequest request, HashMap<String, String> param) {
		// TODO Auto-generated method stub
		return null;
	}
}