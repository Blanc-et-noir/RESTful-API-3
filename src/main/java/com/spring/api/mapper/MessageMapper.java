package com.spring.api.mapper;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.entity.MessageEntity;

@Mapper
public interface MessageMapper {
	public void createMessage(HashMap param);
	public int readNewMessageId();
	public void createMessageSender(HashMap param);
	public void createMessageReceiver(HashMap param);
	public List<MessageEntity> readMessages(HashMap<String, String> param);
	public MessageEntity readMessageByMessageIdAndUserId(HashMap param);
	public void deleteMessageBySenderId(HashMap<String, String> param);
	public void deleteMessageByReceiverId(HashMap<String, String> param);
	public Timestamp readUserMessageTime(HashMap<String, String> param);
	public void updateUserMessageTime(HashMap<String, String> param);
}