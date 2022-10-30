package com.spring.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.service.UserService;
import com.spring.api.util.ResultUtil;

@RestController
public class UserController {
	@Autowired
	UserService userService;
	
	@PostMapping("/api/v1/users")
	public ResponseEntity<HashMap> createUserInfo(@RequestBody HashMap<String,String> param){
		userService.createUser(param);
		
		HashMap result = ResultUtil.createResultMap("회원 가입 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@PutMapping("/api/v1/users/me")
	public ResponseEntity<HashMap> updateMyUserInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.updateMyUserInfo(request,param);
		HashMap result = ResultUtil.createResultMap("회원 정보 변경 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}