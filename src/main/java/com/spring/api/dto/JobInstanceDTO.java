package com.spring.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobInstanceDTO {
	private int job_instance_id;
	private String job_name;
	private String job_key;
}
