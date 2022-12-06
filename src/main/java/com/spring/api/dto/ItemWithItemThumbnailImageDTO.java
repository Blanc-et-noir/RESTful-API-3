package com.spring.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemWithItemThumbnailImageDTO {
	private Integer item_id;
	private String item_name;
	private int item_price;
	private int item_number;
	private String item_time;
	private String item_status;
	private String user_id;
	private Integer item_thumbnail_image_id;
}