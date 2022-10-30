package com.spring.api.dto;

import java.sql.Date;

import com.spring.api.entity.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	private String user_id;
	private String user_name;
	private String user_phone;
	
	private String user_role;
	private String user_cert;
	private String user_status;
	private String user_gender;
	
	private Date user_login_time;
	private Date user_logout_time;
	private Date user_register_time;
	private Date user_withdraw_time;
	private Date user_pass_change_time;
	
	private int question_id;
	
	UserDTO(UserEntity userEntity){
		this.user_id = userEntity.getUser_id();
		this.user_name = userEntity.getUser_name();
		this.user_phone = userEntity.getUser_id();
		this.user_gender = userEntity.getUser_id();
		this.user_role = userEntity.getUser_id();
		this.user_cert = userEntity.getUser_id();
		this.user_status = userEntity.getUser_status();
		this.user_gender = userEntity.getUser_gender();
		
		
		this.user_login_time = userEntity.getUser_login_time();
		this.user_logout_time = userEntity.getUser_logout_time();
		this.user_register_time = userEntity.getUser_register_time();
		this.user_withdraw_time = userEntity.getUser_withdraw_time();
		this.user_pass_change_time = userEntity.getUser_pass_change_time();
		
		this.question_id = userEntity.getQuestion_id();
	}
}
