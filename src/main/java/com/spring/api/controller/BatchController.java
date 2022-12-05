package com.spring.api.controller;

import java.util.Date;
import java.util.HashMap;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.util.ResultUtil;

@RestController

public class BatchController{
	private ResultUtil resultUtil;
	private JobLauncher asyncJobLauncher;
	private Job deleteJob;
	
	@Autowired
	BatchController(ResultUtil resultUtil, JobLauncher asyncJobLauncher, Job deleteJob){
		this.resultUtil = resultUtil;
		this.asyncJobLauncher = asyncJobLauncher;
		this.deleteJob = deleteJob;
	}
	
	@GetMapping("/api/v1/batches")
	public ResponseEntity<HashMap> deleteJob() throws Exception{		
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("now", new Date());
		
		JobExecution jobExecution = asyncJobLauncher.run(deleteJob, jobParametersBuilder.toJobParameters());
		
		HashMap result = resultUtil.createResultMap("배치 작업 성공", true);
		result.put("job_execution_id", jobExecution.getId());
		
		return new ResponseEntity<HashMap>(result, HttpStatus.OK);
	}
}