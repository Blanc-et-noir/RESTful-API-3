package com.spring.api.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
	private String comment_content;
	private String comment_status;
	private String user_id;
	private String user_name;
	private Timestamp comment_time;
	private Integer comment_id;
	private Integer parent_comment_id;
	private Integer item_id;
	private Integer comment_depth;
}