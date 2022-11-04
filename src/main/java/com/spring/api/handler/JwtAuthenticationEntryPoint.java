package com.spring.api.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.spring.api.exception.CustomException;
import com.spring.api.util.TimeUtil;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final TimeUtil timeUtil;
	
	@Autowired
	JwtAuthenticationEntryPoint(TimeUtil timeUtil){
		this.timeUtil = timeUtil;
	}
	
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
    	CustomException customException = (CustomException) request.getAttribute("customException");
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(customException.getHttpStatus().value());
        
    	JSONObject result = new JSONObject();
    	result.put("flag", false);
    	result.put("code", customException.getCode());
    	result.put("message", customException.getMessage());
    	result.put("timestamp", timeUtil.getTimestamp());
    	
        response.getWriter().print(result);
    }
}