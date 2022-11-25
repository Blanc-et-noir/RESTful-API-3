package com.spring.api.dto;

import java.util.List;

import com.spring.api.entity.ItemEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemWithItemImageDTO {
	private Integer item_id;
	private String item_name;
	private int item_price;
	private int item_number;
	private String item_time;
	private String user_id;
	private String user_name;
	private String item_description;
	private List<ItemImageDTO> item_images;
}