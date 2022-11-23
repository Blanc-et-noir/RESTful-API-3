package com.spring.api.dto;

import com.spring.api.entity.CommentEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
	private String comment_content;
	private String user_id;
	private String user_name;
	private String comment_time;
	private int comment_id;
	private int parent_comment_id;
	private int item_id;
	private int comment_depth;
	
	private String nvl(Object obj) {
		return obj!=null?obj.toString():null;
	}
	
	public CommentDTO(CommentEntity commentEntity){
		this.comment_content = commentEntity.getComment_content();
		this.user_id = commentEntity.getUser_id();
		this.user_name = commentEntity.getUser_name();
		this.comment_time = nvl(commentEntity.getComment_time());
		this.parent_comment_id = commentEntity.getParent_comment_id();
		this.item_id = commentEntity.getItem_id();
		this.comment_depth = commentEntity.getComment_depth();
	}
}