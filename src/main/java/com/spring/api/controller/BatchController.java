package com.spring.api.controller;

import java.sql.Timestamp;
import java.util.HashMap;

import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.service.BatchService;
import com.spring.api.util.ResultUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class BatchController{
	private final ResultUtil resultUtil;
	private final BatchService batchService;
	
	@Autowired
	BatchController(BatchService batchService, ResultUtil resultUtil){
		this.batchService = batchService;
		this.resultUtil = resultUtil;
	}
	
	@PostMapping("/api/v1/batches")
	public ResponseEntity<HashMap> createBatch() throws Exception{		
		HashMap result = resultUtil.createResultMap("배치 작업 성공", true);
		
		JobExecution jobExecution = batchService.createBatch();
		
		HashMap batch = new HashMap();
		batch.put("job_execution_id", jobExecution.getId());
		batch.put("jon_instance_id", jobExecution.getJobInstance().getId());
		batch.put("create_time", new Timestamp(jobExecution.getCreateTime().getTime()).toString());
		
		result.put("batch", batch);
		
		return new ResponseEntity<HashMap>(result, HttpStatus.CREATED);
	}
	
	@GetMapping("/api/v1/batches")
	public ResponseEntity<HashMap> readBatches(@RequestParam HashMap param) throws Exception{		
		HashMap result = resultUtil.createResultMap("배치 작업 목록 조회 성공", true);
		
		result.put("batch", batchService.readBatches(param));
		
		return new ResponseEntity<HashMap>(result, HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/batches/{job_execution_id}")
	public ResponseEntity<HashMap> readBatch(@PathVariable("job_execution_id") String job_execution_id){		
		HashMap result = resultUtil.createResultMap("배치 작업 조회 성공", true);
		
		HashMap param = new HashMap();
		param.put("job_execution_id", job_execution_id);
		
		result.put("batch", batchService.readBatch(param));
		
		return new ResponseEntity<HashMap>(result, HttpStatus.OK);
	}
	
	@Scheduled(cron = "0 0 4 * * *")
	private void scheduleBatchJob() throws Exception {
		batchService.createBatch();
	}
}