package com.spring.api.handler;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.spring.api.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
    	CustomException customException = (CustomException) request.getAttribute("customException");
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(customException.getHttpStatus().value());
        
    	JSONObject result = new JSONObject();
    	result.put("flag", false);
    	result.put("code", customException.getCode());
    	result.put("message", customException.getMessage());
    	result.put("timestamp", new Timestamp(System.currentTimeMillis()).toString());
    	
    	log.error("");
    	log.error("[ ERROR ] : flag : {}",false);
    	log.error("[ ERROR ] : code : {}",customException.getCode());
    	log.error("[ ERROR ] : message : {}",customException.getMessage());
    	
        response.getWriter().print(result);
    }
}