package com.spring.api.entity;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageEntity {
	private int message_id;
	private int message_type_id;
	
	private String message_title;
	private String message_content;
	private String message_type_content;
	private String message_sender_user_id;
	private String message_receiver_user_id;
	private String message_sender_status;
	private String message_receiver_status;
	
	private Date message_sender_time;
	private Date message_receiver_time;
}