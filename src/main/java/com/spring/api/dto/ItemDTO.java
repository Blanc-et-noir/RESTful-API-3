package com.spring.api.dto;

import java.sql.Date;
import java.util.List;

import com.spring.api.entity.ItemEntity;
import com.spring.api.entity.ItemImageEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
	private int item_id;
	private String item_name;
	private int item_price;
	private int item_number;
	private Date item_time;
	private String user_id;
	private String user_name;
	private int item_image_id;
	
	public ItemDTO(ItemEntity itemEntity){
		this.item_id = itemEntity.getItem_id();
		this.item_name = itemEntity.getItem_name();
		this.item_price = itemEntity.getItem_price();
		this.item_number = itemEntity.getItem_number();
		this.item_time = itemEntity.getItem_time();
		this.user_id = itemEntity.getUser_id();
		this.item_image_id = itemEntity.getItem_image_id();
		this.user_name = itemEntity.getUser_name();
	}
}