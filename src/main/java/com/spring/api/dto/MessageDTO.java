package com.spring.api.dto;

import java.sql.Timestamp;

import com.spring.api.entity.MessageEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
	private Integer message_id;
	private String message_title;
	private String message_sender_user_id;
	private String message_receiver_user_id;
	private String message_time;
	
	private String nvl(Timestamp timestamp) {
		return timestamp!=null?timestamp.toString():null;
	}
	
	public MessageDTO(MessageEntity messageEntity){
		this.message_id = messageEntity.getMessage_id();
		this.message_title = messageEntity.getMessage_title();
		this.message_sender_user_id = messageEntity.getMessage_sender_user_id();
		this.message_receiver_user_id = messageEntity.getMessage_receiver_user_id();
		this.message_time = nvl(messageEntity.getMessage_time());
	}
}
