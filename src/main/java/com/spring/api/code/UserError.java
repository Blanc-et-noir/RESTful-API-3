package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum UserError implements Code{
	USER_ID_NOT_MATCHED_TO_REGEX("USER_001","사용자 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_PW_NOT_MATCHED_TO_REGEX("USER_002","사용자 PW가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_NAME_NOT_MATCHED_TO_REGEX("USER_003","사용자 이름이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_PHONE_NOT_MATCHED_TO_REGEX("USER_004","사용자 전화번호가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	QUESTION_ID_NOT_MATCHED_TO_REGEX("USER_005","비밀번호 찾기 질문 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	USER_GENDER_NOT_MATCHED_TO_REGEX("USER_006","사용자 성별이 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	QUESTION_ANSWER_EXCEED_MAX_BYTES("USER_007","비밀번호 찾기 질문의 답이 1 ~ 120 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	USER_PW_NOT_EQUAL_TO_USER_PW_CHECK("USER_008","두 비밀번호가 서로 일치하지 않음",HttpStatus.BAD_REQUEST),
	
	DUPLICATE_USER_ID("USER_009","이미 사용중인 사용자 ID",HttpStatus.BAD_REQUEST),
	DUPLICATE_USER_PHONE("USER_010","이미 사용중인 사용자 전화번호",HttpStatus.BAD_REQUEST),
	DUPLICATE_FOLLOWING_INFO("USER_011","이미 팔로우된 사용자",HttpStatus.BAD_REQUEST),
	DUPLICATE_BLOCKING_INFO("USER_012","이미 블락된 사용자",HttpStatus.BAD_REQUEST),
	
	USER_PW_AND_QUESTION_ID_AND_QUESTION_ANSWER_ARE_NEEDED_AT_THE_SAME_TIME("USER_013","비밀번호, 비밀번호 찾기 질문 ID 및 답이 모두 필요함",HttpStatus.BAD_REQUEST),
	
	SOURCE_USER_ID_EQUAL_TO_TARGET_USER_ID("USER_014","다른 사용자의 ID 필요",HttpStatus.BAD_REQUEST),
	QUESTION_ANSWER_NOT_EQUAL_TO_OLD_QUESTION_ANSWER("USER_015","비밀번호 찾기 질문의 답이 서로 일치하지 않음",HttpStatus.BAD_REQUEST),
	QUESTION_ANSWER_IS_NEEDED("USER_016","비밀번호 찾기 질문의 답이 필요함", HttpStatus.BAD_REQUEST),
	NUMBER_OF_FOLLOWING_INFO_EXCEED_LIMIT("USER_017","팔로우 제한 초과", HttpStatus.BAD_REQUEST),
	NUMBER_OF_BLOCKING_INFO_EXCEED_LIMIT("USER_018","블락 제한 초과", HttpStatus.BAD_REQUEST),
	IS_NOT_FOLLOWED_USER_ID("USER_019","팔로우중인 사용자 ID 아님", HttpStatus.BAD_REQUEST),
	IS_NOT_BLOCKED_USER_ID("USER_020","블락중인 사용자 ID 아님", HttpStatus.BAD_REQUEST),
	
	NOT_FOUND_USER("USER_021","사용자 정보 없음", HttpStatus.BAD_REQUEST),
	NOT_FOUND_QUESTION("USER_022","비밀번호 찾기 질문 정보 없음",HttpStatus.BAD_REQUEST),
	
	CAN_NOT_UPDATE_USER_WITHOUT_ANY_CHANGE("USER_023","변경할 정보 없음",HttpStatus.BAD_REQUEST), 
	NOT_LOGGED_IN_USER("USER_024","로그인 상태가 아님",HttpStatus.BAD_REQUEST),
	NOT_IN_USE_USER_ACCESSTOKEN("USER_025","사용중인 액세스 토큰이 아님",HttpStatus.BAD_REQUEST),
	USER_PHONES_NOT_EQUAL_TO_EACH_OTHER("USER_026","회원이 소유한 전화번호가 아님",HttpStatus.BAD_REQUEST);
	
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