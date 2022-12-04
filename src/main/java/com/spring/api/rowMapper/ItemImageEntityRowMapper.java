package com.spring.api.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.spring.api.entity.ItemImageEntity;

public class ItemImageEntityRowMapper implements RowMapper<ItemImageEntity>{

	@Override
	public ItemImageEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		ItemImageEntity itemImageEntity = new ItemImageEntity();
		
		itemImageEntity.setItem_id(rs.getInt("item_id"));
		itemImageEntity.setItem_image_id(rs.getInt("item_image_id"));
		itemImageEntity.setItem_image_original_name(rs.getString("item_image_original_name"));
		itemImageEntity.setItem_image_stored_name(rs.getString("item_image_stored_name"));
		itemImageEntity.setItem_image_extension(rs.getString("item_image_extension"));
		itemImageEntity.setItem_image_size(rs.getInt("item_image_size"));
		itemImageEntity.setItem_image_status(rs.getString("item_image_status"));
		
		return itemImageEntity;
	}
}