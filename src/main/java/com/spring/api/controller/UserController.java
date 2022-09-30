package com.spring.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.service.UserService;

@RestController
public class UserController {
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/api/v1/users",method=RequestMethod.POST)
	public ResponseEntity<HashMap> createUserInfo(@RequestBody HashMap<String,String> param){
		userService.createUser(param);
		
		HashMap result = new HashMap();
		result.put("flag", true);
		result.put("message", "회원 가입 성공");
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@RequestMapping(value="/api/v1/users/me",method=RequestMethod.PUT)
	public ResponseEntity<HashMap> updateMyUserInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.updateMyUserInfo(request,param);
		
		HashMap result = new HashMap();
		result.put("flag", true);
		result.put("message", "회원 정보 변경 성공");
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}