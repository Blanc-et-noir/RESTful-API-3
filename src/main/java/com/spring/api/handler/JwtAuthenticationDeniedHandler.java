package com.spring.api.handler;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.spring.api.code.AuthError;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationDeniedHandler implements AccessDeniedHandler{
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        
    	JSONObject result = new JSONObject();
    	result.put("flag", false);
    	result.put("code", AuthError.NOT_AUTHORIZED.getCode());
    	result.put("message", AuthError.NOT_AUTHORIZED.getMessage());
    	result.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
    	
    	log.error("");
    	log.error("[ ERROR ] : flag : {}",false);
    	log.error("[ ERROR ] : code : {}",AuthError.NOT_AUTHORIZED.getCode());
    	log.error("[ ERROR ] : message : {}",AuthError.NOT_AUTHORIZED.getMessage());
    	
        response.getWriter().print(result);
	}
}