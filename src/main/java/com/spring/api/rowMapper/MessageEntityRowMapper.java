package com.spring.api.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.spring.api.entity.MessageEntity;
import com.spring.api.entity.UserEntity;

public class MessageEntityRowMapper implements RowMapper<MessageEntity>{

	@Override
	public MessageEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		MessageEntity messageEntity = new MessageEntity();
		
		messageEntity.setMessage_content(rs.getString("message_content"));
		messageEntity.setMessage_id(rs.getInt("message_id"));
		messageEntity.setMessage_time(rs.getTimestamp("message_time"));
		messageEntity.setMessage_title(rs.getString("message_title"));
		messageEntity.setMessage_type_id(rs.getInt("message_type_id"));
		
		return messageEntity;
	}
}