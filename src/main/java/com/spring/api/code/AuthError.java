package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum AuthError implements Code{
	
	TOKEN_EXPIRED("AUTH_10000","토큰 만료", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_USER_ACCESSTOKEN("AUTH_10001","액세스 토큰 없음", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_USER_REFRESHTOKEN("AUTH_10002","리프레쉬 토큰 없음", HttpStatus.UNAUTHORIZED),
	
	INVALID_USER_ACCESSTOKEN("AUTH_10003","유효하지 않은 액세스 토큰",HttpStatus.UNAUTHORIZED),
	INVALID_USER_REFRESHTOKEN("AUTH_10004","유효하지 않은 리프레쉬 토큰",HttpStatus.UNAUTHORIZED),
	
	NOT_FOUND_STORED_USER_REFRESHTOKEN("AUTH_10005","저장되지 않은 리프레쉬 토큰",HttpStatus.UNAUTHORIZED);
	
	
	private String code, message;
	private HttpStatus httpStatus;
	
	AuthError(String code, String message, HttpStatus httpStatus){
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}
	
	@Override
	public java.lang.String getCode() {
		return this.code;
	}

	@Override
	public java.lang.String getMessage() {
		return this.message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	} 
}