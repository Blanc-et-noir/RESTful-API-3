package com.spring.api.dto;

import com.spring.api.entity.ItemEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
	private Integer item_id;
	private String item_name;
	private int item_price;
	private int item_number;
	private String item_time;
	private String user_id;
	private String user_name;
	
	private String nvl(Object obj) {
		return obj!=null?obj.toString():null;
	}
	
	public ItemDTO(ItemEntity itemEntity){
		this.item_id = itemEntity.getItem_id();
		this.item_name = itemEntity.getItem_name();
		this.item_price = itemEntity.getItem_price();
		this.item_number = itemEntity.getItem_number();
		this.item_time = nvl(itemEntity.getItem_time());
		this.user_id = itemEntity.getUser_id();
		this.user_name = itemEntity.getUser_name();
	}
}