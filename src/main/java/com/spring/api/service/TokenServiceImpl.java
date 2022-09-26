package com.spring.api.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.spring.api.auth.UserAuthentication;
import com.spring.api.code.AuthError;
import com.spring.api.code.UserError;
import com.spring.api.encrypt.SHA;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.TokenMapper;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.RegexUtil;

@Service("tokenService")
@Transactional
public class TokenServiceImpl implements TokenService{
	@Autowired
	TokenMapper tokenMapper;
	@Autowired
	UserMapper userMapper;
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public HashMap createToken(HttpServletRequest request, HttpServletResponse response, HashMap<String,String> param) throws CustomException {
		String user_id = param.get("user_id");
		String user_pw = param.get("user_pw");
		
		if(!RegexUtil.checkRegex(user_id, RegexUtil.USER_ID_REGEX)) {
			throw new CustomException(UserError.USER_ID_NOT_MATCHED_TO_REGEX);
		}
		
		HashMap<String,String> user = userMapper.readUserByUserId(param);
		
		if(user==null) {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
		
		String user_salt = user.get("user_salt");
		
		if(!RegexUtil.checkRegex(user_pw, RegexUtil.USER_PW_REGEX)) {
			throw new CustomException(UserError.USER_PW_NOT_MATCHED_TO_REGEX);
		}
		
		user_pw = SHA.DSHA512(user_pw, user_salt);
		
		if(user.get("user_pw").equals(user_pw)) {			
			Authentication authentication = new UserAuthentication(user.get("user_id"), null, Arrays.asList(new SimpleGrantedAuthority((String)user.get("user_role"))));
			String user_accesstoken = JwtTokenProvider.createToken(authentication,true);
			String user_refreshtoken = JwtTokenProvider.createToken(authentication,false);
			
			redisTemplate.opsForValue().set(user_id, user_refreshtoken, JwtTokenProvider.getRefreshtokenExpirationTime(),TimeUnit.MILLISECONDS);
			
			response.addHeader("user_accesstoken", user_accesstoken);
			response.addHeader("user_refreshtoken", user_refreshtoken);
		}else {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
		
		user.remove("user_pw");
		user.remove("user_salt");
		user.remove("question_answer");
		
		return user;
	}

	@Override
	public void deleteToken(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = JwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		redisTemplate.delete(user_id);
		redisTemplate.opsForValue().set(user_accesstoken, "invalid", JwtTokenProvider.getAccesstokenExpirationTime(),TimeUnit.MILLISECONDS);
	}

	@Override
	public void updateToken(HttpServletRequest request, HttpServletResponse response) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_refreshtoken = request.getHeader("user_refreshtoken");
		
		if(!StringUtils.hasText(user_accesstoken)) {
			throw new CustomException(AuthError.NOT_FOUND_USER_ACCESSTOKEN);
		}else if(!JwtTokenProvider.validateToken(user_accesstoken)) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}else if(!JwtTokenProvider.getTokenType(user_accesstoken).equals("user_accesstoken")) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}else if(redisTemplate.opsForValue().get(user_accesstoken)!=null) {
			throw new CustomException(AuthError.INVALID_USER_ACCESSTOKEN);
		}
		
		String user_id = JwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String stored_user_refreshtoken = redisTemplate.opsForValue().get(user_id);
		
		if(!StringUtils.hasText(user_refreshtoken)) {
			throw new CustomException(AuthError.NOT_FOUND_USER_REFRESHTOKEN);
		}else if(!JwtTokenProvider.validateToken(user_refreshtoken)) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}else if(!JwtTokenProvider.getTokenType(user_refreshtoken).equals("user_refreshtoken")) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}else if(stored_user_refreshtoken==null) {
			throw new CustomException(AuthError.NOT_FOUND_STORED_USER_REFRESHTOKEN);
		}else if(!stored_user_refreshtoken.equals(user_refreshtoken)) {
			throw new CustomException(AuthError.INVALID_USER_REFRESHTOKEN);
		}
		
		Authentication authentication = new UserAuthentication(user_id, null, null);
		
		String new_user_accesstoken = JwtTokenProvider.createToken(authentication, true);
		String new_user_refreshtoken = JwtTokenProvider.createToken(authentication, false);
		
		redisTemplate.opsForValue().set(user_accesstoken, "invalid", JwtTokenProvider.getAccesstokenExpirationTime(),TimeUnit.MILLISECONDS);
		redisTemplate.opsForValue().set(user_id, new_user_refreshtoken, JwtTokenProvider.getRefreshtokenExpirationTime(),TimeUnit.MILLISECONDS);
		
		response.addHeader("user_accesstoken", new_user_accesstoken);
		response.addHeader("user_refreshtoken", new_user_refreshtoken);
	}
}