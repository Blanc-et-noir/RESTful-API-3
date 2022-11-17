package com.spring.api.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartRequest;

public interface ItemService {
	void createItem(MultipartRequest multipartRequest, HttpServletRequest request);
}
