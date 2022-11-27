package com.spring.api.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<HashMap> deleteItem(HttpServletRequest request, @PathVariable("item_id") String item_id) {
		HashMap result = resultUtil.createResultMap("상품 삭제 성공",true);
		
		HashMap param = new HashMap();
		param.put("item_id", item_id);
		
		itemService.deleteItem(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/items")
	public ResponseEntity<HashMap> readItems(HttpServletRequest request, @RequestParam HashMap param) {
		HashMap result = resultUtil.createResultMap("상품 목록 조회 성공",true);
		result.put("items",itemService.readItems(request, param));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/items/{item_id}/images/{item_image_id}")
	public ResponseEntity readItemImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("item_id") String item_id, @PathVariable("item_image_id") String item_image_id) throws IOException {
		HashMap result = resultUtil.createResultMap("상품 이미지 조회 성공",true);
		
		HashMap param = new HashMap();
		param.put("item_id", item_id);
		param.put("item_image_id", item_image_id);
		
		HttpHeaders header = new HttpHeaders();
		
		header.add("Content-Disposition", "attachment; filename=");
		header.add("Cache-Control", "no-cache");
		
		return itemService.readItemImage(request,response, param);
	}
	
	@PostMapping("/api/v1/items/{item_id}/comments")
	public ResponseEntity<HashMap> createComment(HttpServletRequest request, @PathVariable("item_id") String item_id, @RequestBody HashMap param) {
		HashMap result = resultUtil.createResultMap("상품 댓글 등록 성공",true);
		param.put("item_id", item_id);
		itemService.createComment(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@PostMapping("/api/v1/items/{item_id}/comments/{comment_id}")
	public ResponseEntity<HashMap> createReplyComment(HttpServletRequest request, @PathVariable("item_id") String item_id, @PathVariable("comment_id") String comment_id, @RequestBody HashMap param) {
		HashMap result = resultUtil.createResultMap("상품 답글 등록 성공",true);
		
		param.put("item_id", item_id);
		param.put("comment_id", comment_id);
		
		itemService.createReplyComment(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@GetMapping("/api/v1/items/{item_id}/comments")
	public ResponseEntity<HashMap> readComments(HttpServletRequest request, @PathVariable("item_id") String item_id, @RequestParam HashMap param) {
		HashMap result = resultUtil.createResultMap("상품 댓글 목록 조회 성공",true);
		
		param.put("item_id", item_id);
		
		result.put("comments", itemService.readComments(request, param));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@DeleteMapping("/api/v1/items/{item_id}/comments/{comment_id}")
	public ResponseEntity<HashMap> deleteComment(HttpServletRequest request, @PathVariable("item_id") String item_id, @PathVariable("comment_id") String comment_id) {
		HashMap result = resultUtil.createResultMap("상품 답글 삭제 성공",true);
		
		HashMap param = new HashMap();
		param.put("item_id", item_id);
		param.put("comment_id", comment_id);
		
		itemService.deleteComment(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@PutMapping("/api/v1/items/{item_id}/comments/{comment_id}")
	public ResponseEntity<HashMap> updateComment(HttpServletRequest request, @PathVariable("item_id") String item_id, @PathVariable("comment_id") String comment_id, @RequestBody HashMap param) {
		HashMap result = resultUtil.createResultMap("상품 댓글 수정 성공",true);
		
		param.put("item_id", item_id);
		param.put("comment_id", comment_id);
		
		itemService.updateComment(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
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