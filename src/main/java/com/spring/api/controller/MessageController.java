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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.service.MessageServiceImpl;
import com.spring.api.util.ResultUtil;

@RestController
public class MessageController {
	private final MessageServiceImpl messageService;
	private final ResultUtil resultUtil;
	
	@Autowired
	MessageController(MessageServiceImpl messageService, ResultUtil resultUtil){
		this.messageService = messageService;
		this.resultUtil = resultUtil;
	}
	
	@PostMapping("/api/v1/messages")
	public ResponseEntity<HashMap> createBulkMessage(HttpServletRequest request, @RequestBody HashMap<String,Object> param){
		messageService.createBulkMessage(request,param);
		
		HashMap result = resultUtil.createResultMap("메세지 전송 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.CREATED);
	}
	
	@DeleteMapping("/api/v1/messages/me/{message_id}")
	public ResponseEntity<HashMap> deleteMessage(HttpServletRequest request, @PathVariable("message_id") String message_id){
		
		HashMap result = resultUtil.createResultMap("메세지 삭제 성공",true);
		HashMap param = new HashMap();
		param.put("message_id", message_id);

		messageService.deleteMessage(request, param);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/messages/me/{message_id}")
	public ResponseEntity<HashMap> readMessage(HttpServletRequest request, @RequestParam HashMap<String,String> param, @PathVariable String message_id){
		
		HashMap result = resultUtil.createResultMap("메세지 읽기 성공",true);
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
	
	@GetMapping("/api/v1/messages/me")
	public ResponseEntity<HashMap> readBulkMessage(HttpServletRequest request, @RequestParam HashMap<String,String> param){
		
		HashMap result = resultUtil.createResultMap("메세지 읽기 성공",true);
		result.put("messages", messageService.readBulkMessage(request, param));
		
		return new ResponseEntity<HashMap>(result,HttpStatus.OK);
	}
}