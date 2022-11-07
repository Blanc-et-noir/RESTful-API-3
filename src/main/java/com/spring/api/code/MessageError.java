package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum MessageError implements Code{
	
	ARE_USER_IDS_EMPTY("MESSAGE_10000","사용자 ID 목록 없음", HttpStatus.BAD_REQUEST),
	MESSAGE_TYPE_ID_NOT_MATCHED_TO_REGEX("MESSAGE_10001","메세지 타입 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	MESSAGE_TITLE_EXCEED_MAX_BYTES("MESSAGE_10002","메세지 제목이 1 ~ 120 바이트 크기여야함",HttpStatus.BAD_REQUEST),
	MESSAGE_CONTENT_EXCEED_MAX_BYTES("MESSAGE_10003","메세지 내용이 1 ~ 600 바이트 크기여야함",HttpStatus.BAD_REQUEST),
	NOT_FOUND_MESSAGE_TYPE("MESSAGE_10004","메세지 타입 정보 없음",HttpStatus.BAD_REQUEST);
	
	private String code, message;
	private HttpStatus httpStatus;
	
	MessageError(String code, String message, HttpStatus httpStatus){
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