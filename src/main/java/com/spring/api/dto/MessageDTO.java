package com.spring.api.dto;

import com.spring.api.entity.MessageEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
	private Integer message_id;
	
	private String message_title;
	
	private String message_type_content;
	private String message_sender_user_id;
	private String message_receiver_user_id;
	private String message_sender_user_name;
	private String message_receiver_user_name;
	
	private String message_time;
	
	private String nvl(Object obj) {
		return obj!=null?obj.toString():null;
	}
	
	public MessageDTO(MessageEntity messageEntity){
		this.message_id = messageEntity.getMessage_id();
		this.message_title = messageEntity.getMessage_title();
		this.message_type_content = messageEntity.getMessage_type_content();
		this.message_sender_user_id = messageEntity.getMessage_sender_user_id();
		this.message_receiver_user_id = messageEntity.getMessage_receiver_user_id();
		this.message_time = nvl(messageEntity.getMessage_time());
		this.message_receiver_user_name = messageEntity.getMessage_receiver_user_name();
		this.message_sender_user_name = messageEntity.getMessage_sender_user_name();
	}
}
