package com.spring.api.filter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestWrappingFilter extends OncePerRequestFilter{
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		ContentCachingRequestWrapper readableRequest = new ContentCachingRequestWrapper(request);
        
        Timestamp requestedTime = new Timestamp(System.currentTimeMillis());

        filterChain.doFilter(readableRequest, response);

        Timestamp responsedTime = new Timestamp(System.currentTimeMillis());
        
        try {
        	log.info("");
			log.info("[ INFO ] : URI : {}",readableRequest.getRequestURI());
			log.info("[ INFO ] : Method : {}",readableRequest.getMethod());
			log.info("[ INFO ] : Content Type : {}",readableRequest.getContentType());
			log.info("[ INFO ] : Requested Time : {}",requestedTime);
			log.info("[ INFO ] : Responsed Time : {}",responsedTime);
			log.info("[ INFO ] : Elapsed Time : {}ms",responsedTime.getTime()-requestedTime.getTime());
			log.info("[ INFO ] : Parameters : {}",getParameters(readableRequest));
			log.info("[ INFO ] : Http Status : {}",response.getStatus());
		}catch(Exception e) {
			log.error("[ ERROR ]: "+e);
		}
	}
	
	private String getParameters(ContentCachingRequestWrapper readableRequest) {
		try {
			String contentType = readableRequest.getContentType();
			
			if(contentType == null||contentType.startsWith("multipart/form-data")) {
				return readRequestParameters(readableRequest);
			}else if(contentType.startsWith("application/json")) {
				return readRequestBody(readableRequest);
			}else {
				return null;
			}
		}catch(Exception e) {
			return null;
		}
	}
	
	private String readRequestParameters(ContentCachingRequestWrapper readableRequest) throws IOException {
		HashMap result = new HashMap();
		
		Enumeration<String> enumeration = readableRequest.getParameterNames();
		
		while(enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();
			result.put(parameterName, Arrays.toString(readableRequest.getParameterValues(parameterName)));
		}
		
		return convertHashMapToString(result);
	}
	
	private String readRequestBody(ContentCachingRequestWrapper readableRequest) throws IOException {
		return new ObjectMapper().readTree(readableRequest.getContentAsByteArray()).toString();
	}
	
	private String convertHashMapToString(HashMap hashMap) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(hashMap);
	}
	
	private HashMap convertStringToHashMap(String str) throws JsonProcessingException {
		return new ObjectMapper().readValue(str, HashMap.class);
	}
}