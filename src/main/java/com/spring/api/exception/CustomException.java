package com.spring.api.exception;

import org.springframework.http.HttpStatus;

import com.spring.api.code.Code;

public class CustomException extends RuntimeException{
	Code code;
	
	public CustomException(Code code){
		this.code = code;
	}
	
	public String getMessage() {
		return code.getMessage();
	}
	
	public String getCode() {
		return code.getCode();
	}
	
	public HttpStatus getHttpStatus() {
		return code.getHttpStatus();
	}
}