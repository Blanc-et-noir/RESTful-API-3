package com.spring.api.entity;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemEntity {
	private int item_id;
	private String item_name;
	private String item_description;
	private int item_price;
	private int item_number;
	private Date item_time;
	private String item_status;
	private String user_id;
	private int item_image_id;
}