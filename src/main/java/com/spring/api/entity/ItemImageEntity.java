package com.spring.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemImageEntity {
	private Integer item_id;
	private Integer item_image_id;
	private String item_image_original_name;
	private String item_image_stored_name;
	private String item_image_extension;
	private int item_image_size;
	private String item_image_status;
}