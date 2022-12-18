package com.spring.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobExecutionDTO {
	private int job_execution_id;
	
	private String create_time;
	private String start_time;
	private String end_time;
	
	private String status;
	private String exit_code;
	private String exit_message;
	
	private JobInstanceDTO job_instance;
	private List<StepExecutionDTO> step_executions;
}