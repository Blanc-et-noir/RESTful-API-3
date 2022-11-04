package com.spring.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.service.UserService;
import com.spring.api.util.ResultUtil;

@RestController
public class UserController {
	private final UserService userService;
	private final ResultUtil resultUtil;
	
	@Autowired
	UserController(UserService userService, ResultUtil resultUtil){
		this.userService = userService;
		this.resultUtil = resultUtil;
	}
	
	@PostMapping("/api/v1/users")
	public ResponseEntity<HashMap> createUserInfo(@RequestBody HashMap<String,String> param){
		userService.createUser(param);
		
		HashMap result = resultUtil.createResultMap("회원 가입 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@PutMapping("/api/v1/users/me")
	public ResponseEntity<HashMap> updateMyUserInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.updateMyUserInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 정보 변경 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@PostMapping("/api/v1/users/me/followings")
	public ResponseEntity<HashMap> createFollowingInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.createFollowingInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 팔로우 등록 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@PostMapping("/api/v1/users/me/blockings")
	public ResponseEntity<HashMap> createBlockingInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.createBlockingInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 블락 등록 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/api/v1/users/me/followings")
	public ResponseEntity<HashMap> deleteFollowingInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		//userService.deleteFollowingInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 팔로우 해제 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@DeleteMapping("/api/v1/users/me/blockings")
	public ResponseEntity<HashMap> deleteBlockingInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		//userService.deleteBlockingInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 블락 해제 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}