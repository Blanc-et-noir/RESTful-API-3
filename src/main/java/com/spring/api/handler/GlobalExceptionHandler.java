package com.spring.api.handler;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.spring.api.exception.CustomException;
import com.spring.api.util.ResultUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private final ResultUtil resultUtil;
	
	@Autowired
	GlobalExceptionHandler(ResultUtil resultUtil){
		this.resultUtil = resultUtil;
	}
	
	@ExceptionHandler({CustomException.class})
	public ResponseEntity<HashMap<String,Object>> handleCustomException(CustomException e) {		
		HashMap<String,Object> result = resultUtil.createResultMap(e.getMessage(), false);
		
		result.put("code", e.getCode());
		
    	log.error("");
    	log.error("[ ERROR ] : flag : {}",false);
    	log.error("[ ERROR ] : code : {}",e.getCode());
    	log.error("[ ERROR ] : message : {}",e.getMessage());
		
		return new ResponseEntity<HashMap<String,Object>>(result, e.getHttpStatus());
	}
	
	@ExceptionHandler({Exception.class})
	public ResponseEntity<HashMap<String,Object>> handleException(Exception e) {
		HashMap<String,Object> result = resultUtil.createResultMap("서버 내부 에러", false);

		result.put("code", "SERVER_001");
		
    	log.error("");
    	log.error("[ ERROR ] : flag : {}",false);
    	log.error("[ ERROR ] : code : {}","SERVER_001");
    	log.error("[ ERROR ] : message : {}","서버 내부 에러");

		return new ResponseEntity<HashMap<String,Object>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}