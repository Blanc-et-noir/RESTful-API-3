package com.spring.api.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.spring.api.entity.ItemEntity;

public class ItemEntityRowMapper implements RowMapper<ItemEntity>{

	@Override
	public ItemEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		ItemEntity itemEntity = new ItemEntity();
		
		itemEntity.setItem_id(rs.getInt("item_id"));
		itemEntity.setItem_name(rs.getString("item_name"));
		itemEntity.setItem_number(rs.getInt("item_number"));
		itemEntity.setItem_price(rs.getInt("item_price"));
		itemEntity.setItem_status(rs.getString("item_status"));
		itemEntity.setItem_time(rs.getTimestamp("item_time"));
		itemEntity.setUser_id(rs.getString("user_id"));
		
		return itemEntity;
	}
}