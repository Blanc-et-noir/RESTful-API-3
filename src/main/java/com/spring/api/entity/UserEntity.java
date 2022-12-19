package com.spring.api.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
	private String user_id;
	private String user_pw;
	private String user_name;
	private String user_phone;
	private String user_salt;
	private String user_gender;
	private String user_role;
	private String user_status;
	
	private Timestamp user_login_time;
	private Timestamp user_logout_time;
	private Timestamp user_register_time;
	private Timestamp user_withdraw_time;
	private Timestamp user_pw_change_time;
	
	private int question_id;
	private String question_content;
	private String question_answer;
	
	private String user_accesstoken;
	private String user_refreshtoken;
}
