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
	private Timestamp comment_time;
	private int comment_id;
	private int parent_comment_id;
	private int item_id;
}