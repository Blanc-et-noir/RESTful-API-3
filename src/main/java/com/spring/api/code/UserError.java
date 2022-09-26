package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum UserError implements Code{
	
	NOT_FOUND_USER("USER_10000","사용자 정보 없음", HttpStatus.BAD_REQUEST),
	USER_ID_NOT_MATCHED_TO_REGEX("USER_10001","사용자 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_PW_NOT_MATCHED_TO_REGEX("USER_10002","사용자 PW가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_NAME_NOT_MATCHED_TO_REGEX("USER_10003","사용자 이름이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_PHONE_NOT_MATCHED_TO_REGEX("USER_10004","사용자 전화번호가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	QUESTION_ID_NOT_MATCHED_TO_REGEX("USER_10005","비밀번호 찾기 질문 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	
	QUESTION_ANSWER_EXCEED_MAX_BYTES("USER_10006","비밀번호 찾기 질문의 답이 허용 크기를 초과함", HttpStatus.BAD_REQUEST),
	USER_PW_NOT_EQUAL_TO_USER_PW_CHECK("USER_10007","두 비밀번호가 서로 일치하지 않음",HttpStatus.BAD_REQUEST),
	
	DUPLICATE_USER_ID("USER_10008","이미 사용중인 사용자 ID",HttpStatus.BAD_REQUEST),
	INVALID_QUESTION_ID("USER_10009","유효하지 않은 비밀번호 찾기 질문 ID",HttpStatus.BAD_REQUEST);
	
	
	private String code, message;
	private HttpStatus httpStatus;
	
	UserError(String code, String message, HttpStatus httpStatus){
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