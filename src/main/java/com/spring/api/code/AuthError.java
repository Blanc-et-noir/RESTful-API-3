package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum AuthError implements Code{
	
	NOT_AUTHORIZED("AUTH_001","권한 없음", HttpStatus.UNAUTHORIZED),
	TOKEN_EXPIRED("AUTH_002","토큰 만료", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_USER_ACCESSTOKEN("AUTH_003","액세스 토큰 없음", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_USER_REFRESHTOKEN("AUTH_004","리프레쉬 토큰 없음", HttpStatus.UNAUTHORIZED),
	
	INVALID_USER_ACCESSTOKEN("AUTH_005","유효하지 않은 액세스 토큰",HttpStatus.UNAUTHORIZED),
	INVALID_USER_REFRESHTOKEN("AUTH_006","유효하지 않은 리프레쉬 토큰",HttpStatus.UNAUTHORIZED),
	NOT_FOUND_STORED_USER_ACCESSTOKEN("AUTH_007","현재 사용중인 액세스 토큰 없음",HttpStatus.UNAUTHORIZED),
	NOT_FOUND_STORED_USER_REFRESHTOKEN("AUTH_008","현재 사용중인 리프레쉬 토큰 없음",HttpStatus.UNAUTHORIZED),
	
	NOT_IN_USE_USER_ACCESSTOKEN("AUTH_009","사용중인 액세스 토큰이 아님",HttpStatus.UNAUTHORIZED),
	IS_LOGGED_OUT_ACCESSTOKEN("AUTH_010","로그아웃된 액세스 토큰",HttpStatus.UNAUTHORIZED),
	IS_LOGGED_OUT_REFRESHTOKEN("AUTH_011","로그아웃된 리프레쉬 토큰",HttpStatus.UNAUTHORIZED);
	
	
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