package com.spring.api.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.api.dto.CommentDTO;
import com.spring.api.dto.ItemWithItemImagesDTO;
import com.spring.api.dto.ItemWithItemThumbnailImageDTO;

public interface ItemService {
	public void createItem(MultipartRequest multipartRequest, HttpServletRequest request);
	public List<ItemWithItemThumbnailImageDTO> readItems(HttpServletRequest request, HashMap param);
	public void createComment(HttpServletRequest request, HashMap<String,String> param);
	public void deleteComment(HttpServletRequest request, HashMap<String, String> param);
	public List<CommentDTO> readComments(HttpServletRequest request, HashMap<String,String> param);
	public void createReplyComment(HttpServletRequest request, HashMap<String, String> param);
	public void updateComment(HttpServletRequest request, HashMap<String, String> param);
	public ResponseEntity<Object> readItemImage(HttpServletRequest request, HttpServletResponse response,HashMap<String, String> param) throws IOException;
	public void deleteItem(HttpServletRequest request, HashMap<String, String> param);
	public ItemWithItemImagesDTO readItem(HttpServletRequest request, HashMap<String, String> param);
}