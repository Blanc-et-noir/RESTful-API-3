package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum MessageError implements Code{
	USER_ID_NOT_MATCHED_TO_REGEX("MESSAGE_001","사용자 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	MESSAGE_ID_NOT_MATCHED_TO_REGEX("MESSAGE_002","메세지 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	MESSAGE_TYPE_ID_NOT_MATCHED_TO_REGEX("MESSAGE_003","메세지 타입 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	MESSAGE_BOX_NOT_MATCHED_TO_REGEX("MESSAGE_004","메세지 종류가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	PAGE_NOT_MATCHED_TO_REGEX("MESSAGE_005","메세지 조회 페이지가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	LIMIT_NOT_MATCHED_TO_REGEX("MESSAGE_006","메세지 조회 크기가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	ORDER_NOT_MATCHED_TO_REGEX("MESSAGE_007","메세지 조회 순서가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	FLAG_NOT_MATCHED_TO_REGEX("MESSAGE_008","메세지 검색 기준이 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	
	MESSAGE_TITLE_EXCEED_MAX_BYTES("MESSAGE_009","메세지 제목이 1 ~ 120 바이트 크기여야함",HttpStatus.BAD_REQUEST),
	MESSAGE_CONTENT_EXCEED_MAX_BYTES("MESSAGE_010","메세지 내용이 1 ~ 600 바이트 크기여야함",HttpStatus.BAD_REQUEST),
	SEARCH_EXCEED_MAX_BYTES("MESSAGE_011","메세지 검색 내용이 1 ~ 120 바이트 크기여야함",HttpStatus.BAD_REQUEST),

	NOT_FOUND_MESSAGE("MESSAGE_012","메세지 정보 없음",HttpStatus.BAD_REQUEST),
	NOT_FOUND_MESSAGE_TYPE("MESSAGE_013","메세지 타입 정보 없음",HttpStatus.BAD_REQUEST),
	
	TOO_FREQUENT_TO_SEND_MESSAGE("MESSAGE_014","너무 잦은 메세지 전송 요청",HttpStatus.BAD_REQUEST),
	LIMIT_OUT_OF_RANGE("MESSAGE_015","메세지 조회 크기가 범위를 벗어남",HttpStatus.BAD_REQUEST);
	
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