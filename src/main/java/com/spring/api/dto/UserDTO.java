package com.spring.api.dto;

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
	
	private String user_login_time;
	private String user_logout_time;
	private String user_register_time;
	private String user_withdraw_time;
	private String user_pw_change_time;
	
	private int question_id;
	
	private String nvl(Object obj) {
		return obj!=null?obj.toString():null;
	}
	
	public UserDTO(UserEntity userEntity){
		this.user_id = userEntity.getUser_id();
		this.user_name = userEntity.getUser_name();
		this.user_phone = userEntity.getUser_phone();
		this.user_gender = userEntity.getUser_id();
		this.user_role = userEntity.getUser_role();
		this.user_cert = userEntity.getUser_cert();
		this.user_status = userEntity.getUser_status();
		this.user_gender = userEntity.getUser_gender();
		
		this.user_login_time = nvl(userEntity.getUser_login_time());
		this.user_logout_time = nvl(userEntity.getUser_logout_time());
		this.user_register_time = nvl(userEntity.getUser_register_time());
		this.user_withdraw_time = nvl(userEntity.getUser_withdraw_time());
		this.user_pw_change_time = nvl(userEntity.getUser_pw_change_time());
		
		this.question_id = userEntity.getQuestion_id();
	}
}
