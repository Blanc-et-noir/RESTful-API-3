package com.spring.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemWithItemImagesDTO {
	private Integer item_id;
	private String item_name;
	private int item_price;
	private int item_number;
	private String item_time;
	private String item_description;
	private String item_status;
	private String user_id;
	private List<ItemImageDTO> item_images;
	private List<HashtagDTO> hashtags;
}