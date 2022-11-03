package com.spring.api.dto;

import com.spring.api.entity.FollowingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingDTO {
	private String source_user_id;
	private String target_user_id;
	private String following_time;
	
	private String nvl(Object obj) {
		return obj!=null?obj.toString():null;
	}
	
	FollowingDTO(FollowingEntity followingEntity){
		this.source_user_id = followingEntity.getSource_user_id();
		this.target_user_id = followingEntity.getTarget_user_id();
		this.following_time = nvl(followingEntity.getFollowing_time());
	}
}
