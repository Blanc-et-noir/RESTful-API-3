package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum ItemError implements Code{
	
	//NOT_FOUND_USER("ITEM_10000","사용자 정보 없음", HttpStatus.BAD_REQUEST),
	//USER_ID_NOT_MATCHED_TO_REGEX("ITEM_10001","사용자 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	//USER_PW_NOT_MATCHED_TO_REGEX("ITEM_10002","사용자 PW가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	//USER_NAME_NOT_MATCHED_TO_REGEX("ITEM_10003","사용자 이름이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	//USER_PHONE_NOT_MATCHED_TO_REGEX("ITEM_10004","사용자 전화번호가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	
	
	ITEM_PRICE_NOT_MATCHED_TO_REGEX("ITEM_10002","상품 가격이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	ITEM_NUMBER_NOT_MATCHED_TO_REGEX("ITEM_10003","상품 수량이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	ITEM_PRICE_OUT_OF_RANGE("ITEM_10004","상품 가격은 1 ~ 100,000,000원 범위여야함", HttpStatus.BAD_REQUEST),
	ITEM_NUMBER_OUT_OF_RANGE("ITEM_10005","상품 수량은 1 ~ 100,000,000개 범위여야함", HttpStatus.BAD_REQUEST),
	ITEM_NAME_EXCEED_MAX_BYTES("ITEM_10006","상품 이름은 1 ~ 120 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	ITEM_DESCRIPTION_EXCEED_MAX_BYTES("ITEM_10007","상품 설명은 1 ~ 3000 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	PAGE_NOT_MATCHED_TO_REGEX("ITEM_10006","상품 조회 페이지가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	LIMIT_OUT_OF_RANGE("ITEM_10007","상품 조회 크기가 범위를 벗어남",HttpStatus.BAD_REQUEST),
	LIMIT_NOT_MATCHED_TO_REGEX("ITEM_10008","상품 조회 크기가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	HASHTAG_EXCEED_MAX_BYTES("ITEM_10009","해쉬태그는 1 ~ 120 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	NOT_FOUND_ITEM("ITEM_10010","상품 정보 없음", HttpStatus.BAD_REQUEST), 
	ITEM_ID_NOT_MATCHED_TO_REGEX("ITEM_10011","상품 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST), 
	COMMENT_CONTENT_EXCEED_MAX_BYTES("ITEM_10012","상품 댓글 내용은 1 ~ 600 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	COMMENT_ID_NOT_MATCHED_TO_REGEX("ITEM_10013","상품 댓글 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	NOT_FOUND_COMMENT("ITEM_10014","상품 댓글 정보 없음", HttpStatus.BAD_REQUEST),
	PAGE_OUT_OF_RANGE("ITEM_10015","상품 페이지가 범위를 벗어남",HttpStatus.BAD_REQUEST),
	CAN_NOT_DELETE_OR_UPDATE_COMMENT_BY_USER_ID("ITEM_10016","해당 사용자 정보로 댓글 삭제 또는 수정 불가",HttpStatus.UNAUTHORIZED),
	ITEM_IMAGE_ID_NOT_MATCHED_TO_REGEX("ITEM_10017","상품 이미지 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	NOT_FOUND_ITEM_IMAGE("ITEM_10018","상품 이미지 정보 없음",HttpStatus.BAD_REQUEST), 
	CAN_NOT_DELETE_OR_UPDATE_ITEM_BY_USER_ID("ITEM_10019","해당 사용자 정보로 상품 삭제 또는 수정 불가",HttpStatus.UNAUTHORIZED),
	HASHTAG_CONTENT_NOT_MATCHED_TO_REGEX("ITEM_10020","해시태그 값이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	TOO_FREQUENT_TO_CREATE_ITEM("ITEM_10021","너무 잦은 상품 등록 요청", HttpStatus.BAD_REQUEST),
	ALREADY_SOLD_ITEM("ITEM_10022","이미 판매된 상품", HttpStatus.BAD_REQUEST),
	NOT_FOUND_HASHTAG("ITEM_10023","해쉬태그 정보 없음",HttpStatus.BAD_REQUEST),
	HASHTAG_ID_NOT_MATCHED_TO_REGEX("ITEM_10024","해쉬태그 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	ITEM_IMAGE_TYPE_NOT_MATHCHED_TO_REGEX("ITEM_10025","상품 이미지 타입이 형식에 맞지 않음",HttpStatus.BAD_REQUEST);
	
	private String code, message;
	private HttpStatus httpStatus;
	
	ItemError(String code, String message, HttpStatus httpStatus){
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