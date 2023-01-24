package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum AuthError implements Code{
	NOT_AUTHORIZED("AUTH_001","권한 없음", HttpStatus.UNAUTHORIZED),
	TOKEN_EXPIRED("AUTH_002","토큰 만료", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_USER_ACCESSTOKEN("AUTH_003","액세스 토큰 없음", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_USER_REFRESHTOKEN("AUTH_004","리프레쉬 토큰 없음", HttpStatus.UNAUTHORIZED),
	NOT_FOUND_AUTH_CODE("AUTH_005","해당 전화번호에 대한 인증코드 기한이 만료되었거나 존재하지 않음", HttpStatus.BAD_REQUEST),
	NOT_FOUND_VERIFICATION_CODE("AUTH_006","해당 전화번호에 대한 검증코드 기한이 만료되었거나 존재하지 않음", HttpStatus.BAD_REQUEST),
	
	INVALID_USER_ACCESSTOKEN("AUTH_007","유효하지 않은 액세스 토큰",HttpStatus.UNAUTHORIZED),
	INVALID_USER_REFRESHTOKEN("AUTH_008","유효하지 않은 리프레쉬 토큰",HttpStatus.UNAUTHORIZED),
	NOT_FOUND_STORED_USER_ACCESSTOKEN("AUTH_009","현재 사용중인 액세스 토큰 없음",HttpStatus.UNAUTHORIZED),
	NOT_FOUND_STORED_USER_REFRESHTOKEN("AUTH_010","현재 사용중인 리프레쉬 토큰 없음",HttpStatus.UNAUTHORIZED),
	
	NOT_IN_USE_USER_ACCESSTOKEN("AUTH_011","사용중인 액세스 토큰이 아님",HttpStatus.UNAUTHORIZED),
	IS_LOGGED_OUT_ACCESSTOKEN("AUTH_012","로그아웃된 액세스 토큰",HttpStatus.UNAUTHORIZED),
	IS_LOGGED_OUT_REFRESHTOKEN("AUTH_013","로그아웃된 리프레쉬 토큰",HttpStatus.UNAUTHORIZED),
	
	AUTH_CODE_NOT_SENT("AUTH_014","메세지 발송중 오류 발생",HttpStatus.BAD_REQUEST),
	AUTH_CODE_NOT_EQUAL_TO_STORED_AUTH_CODE("AUTH_015","인증 코드가 일치하지 않음",HttpStatus.BAD_REQUEST),
	AUTH_CODE_NOT_MATCHED_TO_REGEX("AUTH_016","인증 코드가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	VERIFICATION_CODE_NOT_EQUAL_TO_STORED_VERIFICATION_CODE("AUTH_017","검증 코드가 일치하지 않음",HttpStatus.BAD_REQUEST),
	VERIFICATION_CODE_NOT_MATCHED_TO_REGEX("AUTH_018","검증 코드가 형식에 맞지 않음",HttpStatus.BAD_REQUEST);
	
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