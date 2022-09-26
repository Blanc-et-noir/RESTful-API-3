package com.spring.api.filter;

import java.io.IOException;

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
import com.spring.api.jwt.JwtTokenProvider;

public class JwtAuthenticationFilter extends OncePerRequestFilter{
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public JwtAuthenticationFilter(RedisTemplate<String,String> redisTemplate){
		this.redisTemplate = redisTemplate;
	}
	
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	try {
            String user_accesstoken = getJwtFromRequest(request);
            
            if(!StringUtils.hasText(user_accesstoken)) {
            	
            }else if(!JwtTokenProvider.validateToken(user_accesstoken)) {
            	
            }else if(redisTemplate.opsForValue().get(user_accesstoken)!=null){
            	
            }else if(!JwtTokenProvider.getTokenType(user_accesstoken).equals("user_accesstoken")){
            	
            }else {
                String user_id = JwtTokenProvider.getUserIdFromJWT(user_accesstoken);

                UserAuthentication authentication = new UserAuthentication(user_id, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e) {
            e.printStackTrace();
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