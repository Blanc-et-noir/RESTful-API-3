package com.spring.api.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StepExecutionDTO {
	private int step_execution_id;
	private String step_name;
	private String status;
	private String exit_code;
	private Timestamp start_time;
	private Timestamp end_time;
}