package com.spring.api.entity;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockingEntity {
	private String source_user_id;
	private String target_user_id;
	private Timestamp blocking_time;
}
