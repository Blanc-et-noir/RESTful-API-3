package com.spring.api.util;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.api.code.BatchError;
import com.spring.api.code.ItemError;
import com.spring.api.dto.JobExecutionDTO;
import com.spring.api.exception.CustomException;
import com.spring.api.mapper.BatchMapper;

import io.jsonwebtoken.lang.Strings;

@Component
public class BatchCheckUtil {
    private RegexUtil regexUtil;
	private BatchMapper batchMapper;
	
	@Autowired
	BatchCheckUtil(BatchMapper batchMapper, RegexUtil regexUtil){
		this.batchMapper = batchMapper;
		this.regexUtil = regexUtil;
	}
	
	public void checkJobExecutionIdRegex(String job_execution_id) {
		try {
			Integer.parseInt(job_execution_id);
		}catch(Exception e) {
			throw new CustomException(BatchError.JOB_EXECUTION_ID_NOT_MATCHED_TO_REGEX);
		}
	}
	
	public int checkPageRegex(String page) {
		int num = 0;
		
		if(page==null) {
			return 0;
		}
		
		try {
			 num = Integer.parseInt(page);
		}catch(Exception e) {
			throw new CustomException(BatchError.PAGE_NOT_MATCHED_TO_REGEX);
		}
		
		return num;
	}

	public int checkLimitRegex(String limit) {
		int val = 0;
		
		if(limit==null) {
			return regexUtil.getMIN_LIMIT();
		}
		
		try {
			val = Integer.parseInt(limit);
		}catch(Exception e) {			
			throw new CustomException(BatchError.LIMIT_NOT_MATCHED_TO_REGEX);
		}
		
		if(!(val>=regexUtil.getMIN_LIMIT()&&val<=regexUtil.getMAX_LIMIT())) {
			throw new CustomException(BatchError.LIMIT_OUT_OF_RANGE);
		}
		
		return val;
	}

	public JobExecutionDTO isJobExecutionExistent(HashMap param) {
		JobExecutionDTO jobExecutionDTO = batchMapper.readBatch(param);
		if(jobExecutionDTO == null) {
			throw new CustomException(BatchError.NOT_FOUND_JOB_EXECUTION);
		}else {
			return jobExecutionDTO;
		}
	}
	
	public String checkOrderRegex(String order) {
		if(order == null || !Strings.hasText(order) || order.equalsIgnoreCase("desc")) {
			return "desc";
		}else if(order.equalsIgnoreCase("asc")) {
			return "asc";
		}else {
			throw new CustomException(BatchError.ORDER_NOT_MATCHED_TO_REGEX);
		}
	}
}