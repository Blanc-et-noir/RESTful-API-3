package com.spring.api.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.dto.MessageDTO;
import com.spring.api.entity.MessageEntity;

@Mapper
public interface MessageMapper {
	public List<String> readUsersByUserIds(List<String> message_receiver_user_ids);
	public List<String> readUsersByNotBlockedUserIds(HashMap param);
	public void createBulkMessage(HashMap param);
	public int readNewMessageId();
	public HashMap readMessageTypeByMessageTypeId(int message_type_id);
	public void createMessageSenderInfo(HashMap param);
	public void createMessageReceiverInfo(HashMap param);
	public int countMessages(HashMap<String, String> param);
	public List<MessageEntity> readBulkMessage(HashMap<String, String> param);
}