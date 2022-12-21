package com.spring.api.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.dto.JobExecutionDTO;
import com.spring.api.mapper.BatchMapper;
import com.spring.api.util.BatchCheckUtil;

@Service("batchService")
public class BatchServiceImpl implements BatchService{
	private final JobLauncher asyncJobLauncher;
	private final JobExplorer jobExplorer;
	private final Job batchJob;
	private final BatchCheckUtil batchCheckUtil;
	private final BatchMapper batchMapper;
	
	@Autowired
	public BatchServiceImpl(JobLauncher asyncJobLauncher, JobExplorer jobExplorer, Job batchJob, BatchCheckUtil batchCheckUtil, BatchMapper batchMapper) {
		this.asyncJobLauncher = asyncJobLauncher;
		this.jobExplorer = jobExplorer;
		this.batchJob = batchJob;
		this.batchCheckUtil = batchCheckUtil;
		this.batchMapper = batchMapper;
	}
	
	@Override
	public JobExecution createBatch() throws Exception {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addDate("now", new Date());
		
		return asyncJobLauncher.run(batchJob, jobParametersBuilder.toJobParameters());
	}

	@Override
	public List<JobExecutionDTO> readBatches(HashMap param) {
		int page = batchCheckUtil.checkPageRegex((String)param.get("page"));
		int limit = batchCheckUtil.checkLimitRegex((String)param.get("limit"));
		String order = batchCheckUtil.checkOrderRegex((String)param.get("order"));
		
		param.put("page", page);
		param.put("limit", limit);
		param.put("offset", page*limit);
		param.put("order", order);
		
		return batchMapper.readBatches(param);
	}
	
	@Override
	public JobExecutionDTO readBatch(HashMap param) {
		batchCheckUtil.checkJobExecutionIdRegex((String)param.get("job_execution_id"));
		JobExecutionDTO jobExecutionDTO = batchCheckUtil.isJobExecutionExistent(param);
		
		return jobExecutionDTO;
	}
}