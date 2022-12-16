package com.spring.api.dto;

import com.spring.api.entity.BlockingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockingDTO {
	private String source_user_id;
	private String target_user_id;
	private String blocking_time;
	
	private String nvl(Object obj) {
		return obj!=null?obj.toString():null;
	}
	
	public BlockingDTO(BlockingEntity blockingEntity){
		this.source_user_id = blockingEntity.getSource_user_id();
		this.target_user_id = blockingEntity.getTarget_user_id();
		this.blocking_time = nvl(blockingEntity.getBlocking_time());
	}
}
