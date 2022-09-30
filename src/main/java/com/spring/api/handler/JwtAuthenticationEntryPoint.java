package com.spring.api.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.spring.api.exception.CustomException;
import com.spring.api.util.TimeUtil;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        CustomException customException = (CustomException) request.getAttribute("customException");
        
    	JSONObject result = new JSONObject();
    	result.put("flag", false);
    	result.put("code", customException.getCode());
    	result.put("message", customException.getMessage());
    	result.put("timestamp", TimeUtil.getTimestamp());
        response.getWriter().print(result);
    }
}