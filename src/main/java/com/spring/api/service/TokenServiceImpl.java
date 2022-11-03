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

import com.spring.api.auth.UserAuthentication;
import com.spring.api.code.UserError;
import com.spring.api.dto.UserDTO;
import com.spring.api.encrypt.SHA;
import com.spring.api.entity.UserEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.UserMapper;
import com.spring.api.util.CheckUtil;

@Service("tokenService")
@Transactional
public class TokenServiceImpl implements TokenService{
	@Autowired
	private UserMapper userMapper;
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private CheckUtil checkUtil;
	
	@Override
	public void createToken(HttpServletRequest request, HttpServletResponse response, HashMap<String,String> param) throws CustomException {
		String user_id = param.get("user_id");
		String user_pw = param.get("user_pw");
		
		checkUtil.checkUserIdRegex(user_id);
		checkUtil.checkUserPwRegex(user_pw);
		
		UserEntity userEntity = checkUtil.isUserExistent(user_id);
		
		if(!userEntity.getUser_pw().equals(SHA.DSHA512(user_pw, userEntity.getUser_salt()))) {
			throw new CustomException(UserError.NOT_FOUND_USER);
		}
		
		userMapper.updateUserLoginTime(user_id);
		
		Authentication authentication = new UserAuthentication(userEntity.getUser_id(), null, Arrays.asList(new SimpleGrantedAuthority(userEntity.getUser_role())));
		
		String user_accesstoken = jwtTokenProvider.createToken(authentication,true);
		String user_refreshtoken = jwtTokenProvider.createToken(authentication,false);
		
		redisTemplate.opsForValue().set(user_id+"_user_accesstoken", user_accesstoken, jwtTokenProvider.getRefreshtokenExpirationTime(),TimeUnit.MILLISECONDS);
		redisTemplate.opsForValue().set(user_id+"_user_refreshtoken", user_refreshtoken, jwtTokenProvider.getRefreshtokenExpirationTime(),TimeUnit.MILLISECONDS);
		
		response.addHeader("user_accesstoken", user_accesstoken);
		response.addHeader("user_refreshtoken", user_refreshtoken);
		
		return;
	}

	@Override
	public void deleteToken(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		checkUtil.isUserExistent(user_id);

		userMapper.updateUserLogoutTime(user_id);
		
		redisTemplate.delete(user_id+"_user_accesstoken");
		redisTemplate.delete(user_id+"_user_refreshtoken");
		
		return;
	}

	@Override
	public void updateToken(HttpServletRequest request, HttpServletResponse response) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_refreshtoken = request.getHeader("user_refreshtoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		checkUtil.isUserExistent(user_id);

		String stored_user_accesstoken = redisTemplate.opsForValue().get(user_id+"_user_accesstoken");
		String stored_user_refreshtoken = redisTemplate.opsForValue().get(user_id+"_user_refreshtoken");
		
		checkUtil.checkAccessToken(stored_user_accesstoken, user_accesstoken);
		checkUtil.checkRefreshToken(stored_user_refreshtoken, user_refreshtoken);
		
		Authentication authentication = new UserAuthentication(user_id, null, null);
		
		String new_user_accesstoken = jwtTokenProvider.createToken(authentication, true);
		String new_user_refreshtoken = jwtTokenProvider.createToken(authentication, false);
		
		redisTemplate.opsForValue().set(user_id+"_user_accesstoken", new_user_accesstoken, jwtTokenProvider.getRefreshtokenExpirationTime(),TimeUnit.MILLISECONDS);
		redisTemplate.opsForValue().set(user_id+"_user_refreshtoken", new_user_refreshtoken, jwtTokenProvider.getRefreshtokenExpirationTime(),TimeUnit.MILLISECONDS);
		
		response.addHeader("user_accesstoken", new_user_accesstoken);
		response.addHeader("user_refreshtoken", new_user_refreshtoken);
		
		return;
	}

	@Override
	public UserDTO readToken(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		UserEntity userEntity = checkUtil.isUserExistent(user_id);
		
		return new UserDTO(userEntity);
	}
}