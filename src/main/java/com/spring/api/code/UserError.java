package com.spring.api.code;

import org.springframework.http.HttpStatus;

import com.spring.api.util.RegexUtil;

public enum UserError implements Code{
	
	NOT_FOUND_USER("USER_10000","사용자 정보 없음", HttpStatus.BAD_REQUEST),
	USER_ID_NOT_MATCHED_TO_REGEX("USER_10001","사용자 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_PW_NOT_MATCHED_TO_REGEX("USER_10002","사용자 PW가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_NAME_NOT_MATCHED_TO_REGEX("USER_10003","사용자 이름이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_PHONE_NOT_MATCHED_TO_REGEX("USER_10004","사용자 전화번호가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	QUESTION_ID_NOT_MATCHED_TO_REGEX("USER_10005","비밀번호 찾기 질문 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_GENDER_NOT_MATCHED_TO_REGEX("USER_10006","사용자 성별이 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	QUESTION_ANSWER_EXCEED_MAX_BYTES("USER_10007","비밀번호 찾기 질문의 답이 1 ~ "+RegexUtil.QUESTION_ANSWER_MAX_BYTES+" 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	USER_PW_NOT_EQUAL_TO_USER_PW_CHECK("USER_10008","두 비밀번호가 서로 일치하지 않음",HttpStatus.BAD_REQUEST),
	
	DUPLICATE_USER_ID("USER_10009","이미 사용중인 사용자 ID",HttpStatus.BAD_REQUEST),
	DUPLICATE_USER_PHONE("USER_10010","이미 사용중인 사용자 전화번호",HttpStatus.BAD_REQUEST),
	NOT_FOUND_QUESTION("USER_10011","비밀번호 찾기 질문 정보 없음",HttpStatus.BAD_REQUEST),
	USER_PW_AND_QUESTION_ID_AND_QUESTION_ANSWER_ARE_NEEDED_AT_THE_SAME_TIME("USER_10012","비밀번호, 비밀번호 찾기 질문 ID 및 답이 모두 필요함",HttpStatus.BAD_REQUEST),
	QUESTION_ANSWER_NOT_EQUAL_TO_OLD_QUESTION_ANSWER("USER_10013","비밀번호 찾기 질문의 답이 서로 일치하지 않음",HttpStatus.BAD_REQUEST),
	QUESTION_ANSWER_IS_NEEDED("USER_10014","비밀번호 찾기 질문의 답이 필요함", HttpStatus.BAD_REQUEST);
	
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