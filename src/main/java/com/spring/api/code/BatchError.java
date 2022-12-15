package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum BatchError implements Code{
	JOB_EXECUTION_ID_NOT_MATCHED_TO_REGEX("BATCH_001","배치 작업 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	PAGE_NOT_MATCHED_TO_REGEX("BATCH_002","배치 작업 페이지가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	LIMIT_NOT_MATCHED_TO_REGEX("BATCH_003","배치 작업 조회 크기가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	LIMIT_OUT_OF_RANGE("BATCH_004","배치 작업 조회 크기가 범위를 벗어남",HttpStatus.BAD_REQUEST),
	NOT_FOUND_JOB_EXECUTION("BATCH_005","배치 작업 정보 없음",HttpStatus.BAD_REQUEST);
	
	private String code, message;
	private HttpStatus httpStatus;
	
	BatchError(String code, String message, HttpStatus httpStatus){
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