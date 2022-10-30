package com.spring.api.handler;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.spring.api.exception.CustomException;
import com.spring.api.util.TimeUtil;

@Component
public class JwtAuthenticationDeniedHandler implements AccessDeniedHandler{
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        CustomException customException = (CustomException) request.getAttribute("customException");
        
        JSONObject result = new JSONObject();
    	result.put("flag", false);
    	result.put("code", customException.getCode());
    	result.put("message", customException.getMessage());
    	result.put("timestamp", TimeUtil.getTimestamp());

        response.getWriter().print(result);
	}
}