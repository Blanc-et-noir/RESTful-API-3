package com.spring.api.code;

import org.springframework.http.HttpStatus;

public interface Code {
	public String getCode();
	public String getMessage();
	public HttpStatus getHttpStatus();
}