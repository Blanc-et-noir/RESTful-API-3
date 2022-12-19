package com.spring.api.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.spring.api.dto.MessageDTO;

public interface MessageService{	
	public void createMessage(HttpServletRequest request, HashMap<String,String> param);
	public void deleteMessage(HttpServletRequest request, HashMap<String,String> param);
	public List<MessageDTO> readMessages(HttpServletRequest request, HashMap<String,String> param);
}