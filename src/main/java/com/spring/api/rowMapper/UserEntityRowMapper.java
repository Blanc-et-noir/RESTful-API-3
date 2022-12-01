package com.spring.api.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.spring.api.entity.UserEntity;

public class UserEntityRowMapper implements RowMapper<UserEntity>{

	@Override
	public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserEntity userEntity = new UserEntity();
		
		userEntity.setUser_id(rs.getString("user_id"));
		userEntity.setUser_pw(rs.getString("user_pw"));
		userEntity.setUser_name(rs.getString("user_name"));
		userEntity.setUser_phone(rs.getString("user_phone"));
		userEntity.setUser_salt(rs.getString("user_salt"));
		userEntity.setUser_gender(rs.getString("user_gender"));
		userEntity.setUser_role(rs.getString("user_role"));
		userEntity.setUser_cert(rs.getString("user_salt"));
		userEntity.setUser_status(rs.getString("user_status"));
		userEntity.setQuestion_content(rs.getString("user_name"));
		userEntity.setQuestion_answer(rs.getString("question_answer"));
		userEntity.setUser_accesstoken(rs.getString("user_accesstoken"));
		userEntity.setUser_refreshtoken(rs.getString("user_refreshtoken"));
		
		
		userEntity.setQuestion_id(rs.getInt("question_id"));
		
		userEntity.setUser_login_time(rs.getTimestamp("user_login_time"));
		userEntity.setUser_logout_time(rs.getTimestamp("user_logout_time"));
		userEntity.setUser_register_time(rs.getTimestamp("user_register_time"));
		userEntity.setUser_withdraw_time(rs.getTimestamp("user_withdraw_time"));
		userEntity.setUser_pw_change_time(rs.getTimestamp("user_pw_change_time"));
		
		return userEntity;
	}
}