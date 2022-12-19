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
	private String user_status;
	private String user_gender;
	private String question_content;
	private Integer question_id;
	
	public UserDTO(UserEntity userEntity){
		this.user_id = userEntity.getUser_id();
		this.user_name = userEntity.getUser_name();
		this.user_phone = userEntity.getUser_phone();
		this.user_gender = userEntity.getUser_id();
		this.user_role = userEntity.getUser_role();
		this.user_status = userEntity.getUser_status();
		this.user_gender = userEntity.getUser_gender();
		this.question_id = userEntity.getQuestion_id();
		this.question_content = userEntity.getQuestion_content();
	}
}
