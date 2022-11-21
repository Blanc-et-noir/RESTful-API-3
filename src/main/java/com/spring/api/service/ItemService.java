package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartRequest;

import com.spring.api.dto.ItemDTO;
import com.spring.api.entity.ItemEntity;

public interface ItemService {
	public void createItem(MultipartRequest multipartRequest, HttpServletRequest request);
	public List<ItemDTO> readItems(HttpServletRequest request, HashMap param);
	public void createComment(HttpServletRequest request, HashMap<String,String> param);
	public void createReplyComment(HttpServletRequest request, HashMap param);
}