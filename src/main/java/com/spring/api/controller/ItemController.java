package com.spring.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.api.service.ItemServiceImpl;
import com.spring.api.util.ResultUtil;

@RestController
public class ItemController {
	private final ItemServiceImpl itemService;
	private final ResultUtil resultUtil;
	
	@Autowired
	ItemController(ItemServiceImpl itemService, ResultUtil resultUtil){
		this.itemService = itemService;
		this.resultUtil = resultUtil;
	}
	
	@PostMapping("/api/v1/items")
	public ResponseEntity<HashMap> createItem(MultipartRequest multipartRequest, HttpServletRequest request) {
		
		HashMap result = resultUtil.createResultMap("상품 등록 성공",true);
		itemService.createItem(multipartRequest, request);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@PutMapping("/api/v1/items/{item_id}")
	public void updateItem(MultipartRequest multipartRequest, HttpServletRequest request, @PathVariable("item_id") String item_id) {
		
	}
	
	@DeleteMapping("/api/v1/items/{item_id}")
	public void deleteItem(HttpServletRequest request, @RequestParam HashMap param, @PathVariable("item_id") String item_id) {
		
	}
	
	@GetMapping("/api/v1/items")
	public void readItems(HttpServletRequest request, @RequestParam HashMap param) {
		
	}
	
	@GetMapping("/api/v1/items{item_id}/images")
	public void readItemImages(HttpServletRequest request) {
		
	}
	
	@GetMapping("/api/v1/items{item_id}/images/{item_image_id}")
	public void readItemImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("item_id") String item_id, @PathVariable("item_image_id") String item_image_id) {
		
	}
	
	@PostMapping("/api/v1/items/{item_id}/comments")
	public void createComment(HttpServletRequest request, @PathVariable("item_id") String item_id) {
		
	}
	
	@GetMapping("/api/v1/items/{item_id}/comments")
	public void readComment(HttpServletRequest request, @PathVariable("item_id") String item_id) {
		
	}
	
	@DeleteMapping("/api/v1/items/{item_id}/comments/{comment_id}")
	public void deleteComment(HttpServletRequest request, @PathVariable("item_id") String item_id, @PathVariable("comment_id") String comment_id) {
		
	}
	
	@PutMapping("/api/v1/items/{item_id}/comments/{comment_id}")
	public void updateComment(HttpServletRequest request, @PathVariable("item_id") String item_id, @PathVariable("comment_id") String comment_id) {
		
	}
	
	@PostMapping("/api/v1/wishlists")
	public void createWishlist(HttpServletRequest request) {
		
	}
	
	@GetMapping("/api/v1/wishlists/me")
	public void readWishlists(HttpServletRequest request) {
		
	}
	
	@DeleteMapping("/api/v1/wishlists/{wishlist_id}")
	public void deleteWishlist(HttpServletRequest request,  @PathVariable("wishlist_id") String wishlist_id) {
		
	}
}