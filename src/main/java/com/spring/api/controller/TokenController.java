package com.spring.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.service.TokenService;
import com.spring.api.util.ResultUtil;

@RestController
public class TokenController {
	@Autowired
	TokenService tokenService;

	@PostMapping("/api/v1/tokens")
	public ResponseEntity<HashMap> createToken(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String,String> param){
		tokenService.createToken(request, response, param);
		
		HashMap result = ResultUtil.createResultMap("토큰 발급 성공");
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@GetMapping("/api/v1/tokens")
	public ResponseEntity<HashMap> readToken(HttpServletRequest request){
		HashMap result = ResultUtil.createResultMap("토큰 조회 성공");
		
		result.put("user_id", JwtTokenProvider.getUserIdFromJWT(request.getHeader("user_accesstoken")));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@PutMapping("/api/v1/tokens")
	public ResponseEntity<HashMap> updateToken(HttpServletRequest request, HttpServletResponse response){
		tokenService.updateToken(request, response);
		
		HashMap result = ResultUtil.createResultMap("토큰 갱신 성공");

		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@DeleteMapping("/api/v1/tokens")
	public ResponseEntity<HashMap> deleteToken(HttpServletRequest request){
		tokenService.deleteToken(request);
		
		HashMap result = ResultUtil.createResultMap("토큰 삭제 성공");
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}