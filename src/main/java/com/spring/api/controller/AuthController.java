package com.spring.api.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.service.AuthService;
import com.spring.api.util.ResultUtil;

@RestController
public class AuthController {
	private final AuthService authService;
	private final ResultUtil resultUtil;
	
	@Autowired
	AuthController(AuthService authService, ResultUtil resultUtil){
		this.authService = authService;
		this.resultUtil = resultUtil;
	}
	
	@PostMapping("/api/v1/auth")
	public ResponseEntity<HashMap> getAuthcode(@RequestBody HashMap<String,String> param) throws Exception{
		HashMap result = resultUtil.createResultMap("인증코드 요청 성공",true);
		
		authService.getAuthcode(param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}