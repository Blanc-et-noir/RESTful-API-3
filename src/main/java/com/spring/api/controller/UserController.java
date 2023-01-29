package com.spring.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@GetMapping("/api/v1/users/user-ids")
	public ResponseEntity<HashMap> findUserId(@RequestParam HashMap param){
		HashMap result = resultUtil.createResultMap("회원 ID 조회 성공",true);
		result.put("user_id", userService.findUserId(param));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/checks/users/{user_id}")
	public ResponseEntity<HashMap> checkDuplicateUserId(@PathVariable("user_id") String user_id){
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		
		userService.checkDuplicateUserId(param);
		
		HashMap result = resultUtil.createResultMap("중복되지 않은 회원 ID",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/checks/users/phones/{user_phone}")
	public ResponseEntity<HashMap> checkDuplicateUserPhone(@PathVariable("user_phone") String user_phone){
		HashMap param = new HashMap();
		param.put("user_phone", user_phone);
		
		userService.checkDuplicateUserPhone(param);
		
		HashMap result = resultUtil.createResultMap("중복되지 않은 회원 전화번호",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
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
	
	@GetMapping("/api/v1/users/me")
	public ResponseEntity<HashMap> updateMyUserInfo(HttpServletRequest request){		
		HashMap result = resultUtil.createResultMap("회원 정보 조회 성공",true);
		
		result.put("user", userService.readMyUserInfo(request));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@PostMapping("/api/v1/users/me/blockings")
	public ResponseEntity<HashMap> createBlockingInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.createBlockingInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 블락 등록 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/api/v1/users/me/blockings")
	public ResponseEntity<HashMap> deleteBlockingInfo(HttpServletRequest request, @RequestBody HashMap<String,String> param){
		userService.deleteBlockingInfo(request,param);
		
		HashMap result = resultUtil.createResultMap("회원 블락 해제 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/users/me/blockings")
	public ResponseEntity<HashMap> readBlockingInfo(HttpServletRequest request){
		HashMap result = resultUtil.createResultMap("회원 블락 목록 조회 성공",true);
		
		result.put("blockings", userService.readBlockingInfo(request));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@DeleteMapping("/api/v1/users/me")
	public ResponseEntity<HashMap> deleteMyUserInfo(HttpServletRequest request){
		
		HashMap result = resultUtil.createResultMap("회원 탈퇴 성공",true);
		
		userService.deleteMyUserInfo(request);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/users/{user_id}/questions")
	public ResponseEntity<HashMap> readQuestion(HttpServletRequest request, @PathVariable("user_id") String user_id){
		HashMap result = resultUtil.createResultMap("비밀번호 찾기 질문 조회 성공",true);
		
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		
		result.put("question", userService.readQuestion(request, param));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/questions")
	public ResponseEntity<HashMap> readQuestions(){
		HashMap result = resultUtil.createResultMap("비밀번호 찾기 질문 목록 조회 성공",true);
		
		result.put("questions", userService.readQuestions());
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@PutMapping("/api/v1/users/{user_id}/passwords")
	public ResponseEntity<HashMap> updateUserPw(HttpServletRequest request, @PathVariable("user_id") String user_id, @RequestBody HashMap param){
		HashMap result = resultUtil.createResultMap("사용자 비밀번호 변경 성공",true);
		
		param.put("user_id", user_id);
		
		userService.updateUserPw(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}