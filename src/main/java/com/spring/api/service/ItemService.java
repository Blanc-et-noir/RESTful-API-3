package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartRequest;

import com.spring.api.dto.ItemDTO;
import com.spring.api.entity.ItemEntity;

public interface ItemService {
	void createItem(MultipartRequest multipartRequest, HttpServletRequest request);
	List<ItemDTO> readItems(HttpServletRequest request, HashMap param);
}