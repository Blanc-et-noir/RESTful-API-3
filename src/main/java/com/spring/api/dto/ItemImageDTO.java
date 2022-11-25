package com.spring.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemImageDTO {
	Integer item_id;
	Integer item_image_id;
	Integer item_image_size;
	String item_image_original_name;
	String item_image_stored_name;
	String item_image_extension;
}
