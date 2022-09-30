package com.spring.api.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.api.auth.UserAuthentication;
import com.spring.api.code.AuthError;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.util.TimeUtil;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public JwtAuthenticationFilter(RedisTemplate<String,String> redisTemplate){
		this.redisTemplate = redisTemplate;
	}
	
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	String user_accesstoken = getJwtFromRequest(request);
        String stored_user_accesstoken = null;
        
    	String user_id = null;
    	
        if(!StringUtils.hasText(user_accesstoken)) {
        	request.setAttribute("customException", new CustomException(AuthError.NOT_FOUND_USER_ACCESSTOKEN));
        }else if(!JwtTokenProvider.validateToken(user_accesstoken)) {
        	request.setAttribute("customException", new CustomException(AuthError.INVALID_USER_ACCESSTOKEN));
        }else {
            user_id = JwtTokenProvider.getUserIdFromJWT(user_accesstoken);
            
            if(!JwtTokenProvider.getTokenType(user_accesstoken).equals("user_accesstoken")) {
            	request.setAttribute("customException", new CustomException(AuthError.INVALID_USER_ACCESSTOKEN));
            }else if((stored_user_accesstoken=redisTemplate.opsForValue().get(user_id+"_user_accesstoken"))==null){
            	request.setAttribute("customException", new CustomException(AuthError.NOT_FOUND_STORED_USER_ACCESSTOKEN));
            }else if(!stored_user_accesstoken.equals(user_accesstoken)) {
            	request.setAttribute("customException", new CustomException(AuthError.NOT_IN_USE_USER_ACCESSTOKEN));
            }else {
                UserAuthentication authentication = new UserAuthentication(user_id, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String user_accesstoken = request.getHeader("user_accesstoken");
        
        if (StringUtils.hasText(user_accesstoken)) {
            return user_accesstoken;
        }
        
        return null;
    }
}