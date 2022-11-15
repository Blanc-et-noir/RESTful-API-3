package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.spring.api.dto.DetailedMessageDTO;
import com.spring.api.dto.MessageDTO;

public interface MessageService{	
	public void createBulkMessage(HttpServletRequest request, HashMap<String,Object> param);
	public void deleteMessage(HttpServletRequest request, HashMap<String,String> param);
	public DetailedMessageDTO readMessage(HttpServletRequest request, HashMap<String,String> param);
	public List<MessageDTO> readBulkMessage(HttpServletRequest request, HashMap<String,String> param);
}