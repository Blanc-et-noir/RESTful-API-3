package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.batch.core.JobExecution;

import com.spring.api.dto.JobExecutionDTO;

public interface BatchService {
	public JobExecution createBatch() throws Exception;
	public List<JobExecutionDTO> readBatches(HashMap param);
	public JobExecutionDTO readBatch(HashMap param);
}