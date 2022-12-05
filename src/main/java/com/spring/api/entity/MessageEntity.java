package com.spring.api.entity;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageEntity {
	private Integer message_id;
	private Integer message_type_id;
	
	private String message_title;
	private String message_content;
	private String message_type_content;
	private String message_sender_user_id;
	private String message_receiver_user_id;
	private String message_sender_status;
	private String message_receiver_status;
	
	private Timestamp message_sender_delete_time;
	private Timestamp message_receiver_delete_time;
	private Timestamp message_time;
}