package com.spring.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HashtagDTO {
	private Integer hashtag_id;
	private Integer item_id;
	private String hashtag_content;
}