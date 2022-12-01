package com.spring.api.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.api.auth.UserAuthentication;
import com.spring.api.code.AuthError;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.util.RedisUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final RedisUtil redisUtil;
	private final JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	public JwtAuthenticationFilter(RedisUtil redisUtil, JwtTokenProvider jwtTokenProvider){
		this.redisUtil = redisUtil;
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	String user_accesstoken = getUserAccesstokenFromRequest(request);

        if(!StringUtils.hasText(user_accesstoken)) {
        	request.setAttribute("customException", new CustomException(AuthError.NOT_FOUND_USER_ACCESSTOKEN));
        	//throw new CustomException(AuthError.NOT_FOUND_USER_ACCESSTOKEN);
        }else if(!jwtTokenProvider.validateToken(user_accesstoken)) {
        	request.setAttribute("customException", new CustomException(AuthError.INVALID_USER_ACCESSTOKEN));
        	//throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
        }else {
            String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
            String user_role = jwtTokenProvider.getUserRoleFromJWT(user_accesstoken);
            
            if(!jwtTokenProvider.getTokenType(user_accesstoken).equals("user_accesstoken")) {
            	request.setAttribute("customException", new CustomException(AuthError.INVALID_USER_ACCESSTOKEN));
            	//throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
            }else if(redisUtil.getData(user_accesstoken)!=null){
            	request.setAttribute("customException", new CustomException(AuthError.IS_LOGGED_OUT_ACCESSTOKEN));
            	//throw new CustomException(AuthError.IS_LOGGED_OUT_ACCESSTOKEN);
            }else {
                UserAuthentication authentication = new UserAuthentication(user_id, null, Arrays.asList(new SimpleGrantedAuthority(user_role)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getUserAccesstokenFromRequest(HttpServletRequest request) {
        String user_accesstoken = request.getHeader("user_accesstoken");
        
        if (StringUtils.hasText(user_accesstoken)) {
            return user_accesstoken;
        }
        
        return null;
    }
}