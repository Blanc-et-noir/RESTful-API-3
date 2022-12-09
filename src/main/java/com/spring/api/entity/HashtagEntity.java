package com.spring.api.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashtagEntity {
	private Integer hashtag_id;
	private Integer item_id;
	private String hashtag_content;
}