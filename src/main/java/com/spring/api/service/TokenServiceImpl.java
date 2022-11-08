package com.spring.api.service;

import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.spring.api.util.RedisUtil;
import com.spring.api.util.UserCheckUtil;

@Service("tokenService")
@Transactional
public class TokenServiceImpl implements TokenService{
	private final UserMapper userMapper;
    private final RedisUtil redisUtil;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserCheckUtil checkUtil;
	
	@Autowired
	TokenServiceImpl(UserMapper userMapper, JwtTokenProvider jwtTokenProvider, RedisUtil redisUtil, UserCheckUtil checkUtil){
		this.userMapper = userMapper;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisUtil = redisUtil;
		this.checkUtil = checkUtil;
	}
	
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
		
		Authentication authentication = new UserAuthentication(userEntity.getUser_id(), null, Arrays.asList(new SimpleGrantedAuthority(userEntity.getUser_role())));
		
		String user_accesstoken = jwtTokenProvider.createToken(authentication,true);
		String user_refreshtoken = jwtTokenProvider.createToken(authentication,false);
		
		String old_user_accesstoken = userEntity.getUser_accesstoken();
		String old_user_refreshtoken = userEntity.getUser_refreshtoken();
		
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);
		param.put("user_id", user_id);
		userMapper.updateUserLoginTime(param);
		userMapper.updateUserTokensToNewTokens(param);
		
		redisUtil.setData(old_user_accesstoken, "removed", jwtTokenProvider.getRemainingTime(old_user_accesstoken));
		redisUtil.setData(old_user_refreshtoken, "removed", jwtTokenProvider.getRemainingTime(old_user_refreshtoken));
		
		response.addHeader("user_accesstoken", user_accesstoken);
		response.addHeader("user_refreshtoken", user_refreshtoken);
		
		return;
	}

	@Override
	public void deleteToken(HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		UserEntity userEntity = checkUtil.isUserExistent(user_id);

		String old_user_accesstoken = userEntity.getUser_accesstoken();
		String old_user_refreshtoken = userEntity.getUser_refreshtoken();
		
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		
		userMapper.updateUserLogoutTime(user_id);
		userMapper.updateUserTokensToNull(param);
		
		redisUtil.setData(old_user_accesstoken, "removed", jwtTokenProvider.getRemainingTime(old_user_accesstoken));
		redisUtil.setData(old_user_refreshtoken, "removed", jwtTokenProvider.getRemainingTime(old_user_refreshtoken));
		
		return;
	}

	@Override
	public void updateToken(HttpServletRequest request, HttpServletResponse response) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_refreshtoken = request.getHeader("user_refreshtoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		UserEntity userEntity = checkUtil.isUserExistent(user_id);
		
		String old_user_accesstoken = userEntity.getUser_accesstoken();
		String old_user_refreshtoken = userEntity.getUser_refreshtoken();
		
		HashMap param = new HashMap();
		param.put("user_id", user_id);
		param.put("user_accesstoken", user_accesstoken);
		param.put("user_refreshtoken", user_refreshtoken);

		userMapper.updateUserTokensToNewTokens(param);
		
		checkUtil.checkAccessToken(old_user_accesstoken, user_accesstoken);
		checkUtil.checkRefreshToken(old_user_refreshtoken, user_refreshtoken);
		
		Authentication authentication = new UserAuthentication(user_id, null, null);
		
		String new_user_accesstoken = jwtTokenProvider.createToken(authentication, true);
		String new_user_refreshtoken = jwtTokenProvider.createToken(authentication, false);
		
		response.addHeader("user_accesstoken", new_user_accesstoken);
		response.addHeader("user_refreshtoken", new_user_refreshtoken);
		
		redisUtil.setData(old_user_accesstoken, "removed", jwtTokenProvider.getRemainingTime(old_user_accesstoken));
		redisUtil.setData(old_user_refreshtoken, "removed", jwtTokenProvider.getRemainingTime(old_user_refreshtoken));
		
		return;
	}

	@Override
	public HashMap readToken(HttpServletRequest request) {
		String user_token = request.getHeader("user_token");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_token);

		HashMap tokenInfo = new HashMap();
		tokenInfo.put("token_owner", jwtTokenProvider.getUserIdFromJWT(user_token));
		tokenInfo.put("token_type", jwtTokenProvider.getTokenType(user_token));
		tokenInfo.put("token_remaining_time_of_seconds", jwtTokenProvider.getRemainingTime(user_token)/1000);
		
		if(redisUtil.getData(user_token)!=null) {
			tokenInfo.put("token_logout", true);
		}else {
			tokenInfo.put("token_logout", false);
		}
		
		return tokenInfo;
	}
}