package com.spring.api.dto;

import java.sql.Date;

import com.spring.api.entity.MessageEntity;

public class MessageDTO {
	private int message_id;
	private int message_type_id;
	
	private String message_title;
	private String message_content;
	private String message_type_content;
	private String message_sender_user_id;
	private String message_receiver_user_id;
	
	private Date message_sender_time;
	private Date message_receiver_time;
	
	MessageDTO(MessageEntity messageEntity){
		this.message_id = messageEntity.getMessage_id();
		this.message_type_id = messageEntity.getMessage_type_id();
		this.message_title = messageEntity.getMessage_title();
		this.message_content = messageEntity.getMessage_content();
		this.message_type_content = messageEntity.getMessage_type_content();
		this.message_sender_user_id = messageEntity.getMessage_sender_user_id();
		this.message_receiver_user_id = messageEntity.getMessage_receiver_user_id();
		this.message_sender_time = messageEntity.getMessage_sender_time();
		this.message_receiver_time = messageEntity.getMessage_receiver_time();
	}
}
