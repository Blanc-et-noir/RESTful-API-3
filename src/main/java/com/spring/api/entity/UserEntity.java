package com.spring.api.entity;

import java.sql.Date;

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
	private String user_cert;
	private String user_status;
	
	private Date user_login_time;
	private Date user_logout_time;
	private Date user_register_time;
	private Date user_withdraw_time;
	private Date user_pass_change_time;
	
	private int question_id;
	private String question_answer;
}