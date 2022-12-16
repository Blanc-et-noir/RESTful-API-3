package com.spring.api.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.dto.JobExecutionDTO;

@Mapper
public interface BatchMapper {
	public JobExecutionDTO readBatch(HashMap param);
	public List<JobExecutionDTO> readBatches(HashMap param);
}