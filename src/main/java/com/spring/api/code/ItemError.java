package com.spring.api.code;

import org.springframework.http.HttpStatus;

public enum ItemError implements Code{
	ITEM_ID_NOT_MATCHED_TO_REGEX("ITEM_001","상품 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	ITEM_PRICE_NOT_MATCHED_TO_REGEX("ITEM_002","상품 가격이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	ITEM_NUMBER_NOT_MATCHED_TO_REGEX("ITEM_003","상품 수량이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	PAGE_NOT_MATCHED_TO_REGEX("ITEM_004","상품 조회 페이지가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	LIMIT_NOT_MATCHED_TO_REGEX("ITEM_005","상품 조회 크기가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	COMMENT_ID_NOT_MATCHED_TO_REGEX("ITEM_006","상품 댓글 ID가 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	ITEM_IMAGE_ID_NOT_MATCHED_TO_REGEX("ITEM_007","상품 이미지 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	HASHTAG_CONTENT_NOT_MATCHED_TO_REGEX("ITEM_008","해시태그 값이 형식에 맞지 않음", HttpStatus.BAD_REQUEST),
	HASHTAG_ID_NOT_MATCHED_TO_REGEX("ITEM_009","해쉬태그 ID가 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	ITEM_IMAGE_TYPE_NOT_MATHCHED_TO_REGEX("ITEM_010","상품 이미지 타입이 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	FLAG_NOT_MATCHED_TO_REGEX("ITEM_011","상품 조회 기준이 형식에 맞지 않음",HttpStatus.BAD_REQUEST),
	
	LIMIT_OUT_OF_RANGE("ITEM_012","상품 조회 크기가 범위를 벗어남",HttpStatus.BAD_REQUEST),
	ITEM_PRICE_OUT_OF_RANGE("ITEM_013","상품 가격은 1 ~ 100,000,000원 범위여야함", HttpStatus.BAD_REQUEST),
	ITEM_NUMBER_OUT_OF_RANGE("ITEM_014","상품 수량은 1 ~ 100,000,000개 범위여야함", HttpStatus.BAD_REQUEST),
	
	ITEM_NAME_EXCEED_MAX_BYTES("ITEM_015","상품 이름은 1 ~ 120 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	ITEM_DESCRIPTION_EXCEED_MAX_BYTES("ITEM_016","상품 설명은 1 ~ 3000 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	HASHTAG_EXCEED_MAX_BYTES("ITEM_017","해쉬태그는 1 ~ 120 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	COMMENT_CONTENT_EXCEED_MAX_BYTES("ITEM_018","상품 댓글 내용은 1 ~ 600 바이트 크기여야함", HttpStatus.BAD_REQUEST),
	
	NOT_FOUND_ITEM("ITEM_019","상품 정보 없음", HttpStatus.BAD_REQUEST), 
	NOT_FOUND_COMMENT("ITEM_020","상품 댓글 정보 없음", HttpStatus.BAD_REQUEST),
	NOT_FOUND_ITEM_IMAGE("ITEM_021","상품 이미지 정보 없음",HttpStatus.BAD_REQUEST), 
	NOT_FOUND_HASHTAG("ITEM_022","해쉬태그 정보 없음",HttpStatus.BAD_REQUEST),
	
	CAN_NOT_DELETE_OR_UPDATE_COMMENT_BY_USER_ID("ITEM_023","해당 사용자 정보로 댓글 삭제 또는 수정 불가",HttpStatus.UNAUTHORIZED),
	CAN_NOT_DELETE_OR_UPDATE_ITEM_BY_USER_ID("ITEM_024","해당 사용자 정보로 상품 삭제 또는 수정 불가",HttpStatus.UNAUTHORIZED),
	
	TOO_FREQUENT_TO_CREATE_ITEM("ITEM_025","너무 잦은 상품 등록 요청", HttpStatus.BAD_REQUEST),
	ALREADY_SOLD_ITEM("ITEM_026","이미 판매된 상품", HttpStatus.BAD_REQUEST);
	
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