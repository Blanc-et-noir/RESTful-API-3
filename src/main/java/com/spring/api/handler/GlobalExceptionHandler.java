package com.spring.api.handler;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.spring.api.exception.CustomException;
import com.spring.api.util.ResultUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler({CustomException.class})
	public ResponseEntity<HashMap> handleCustomException(CustomException e) {		
		HashMap result = ResultUtil.createResultMap(e.getMessage(), false);
		
		result.put("code", e.getCode());
		
		return new ResponseEntity<HashMap>(result, e.getHttpStatus());
	}
	
	@ExceptionHandler({Exception.class})
	public ResponseEntity<HashMap> handleException(Exception e) {
		HashMap result = ResultUtil.createResultMap("서버 내부 에러", false);

		result.put("code", "SERVER_10000");

		return new ResponseEntity<HashMap>(result, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}