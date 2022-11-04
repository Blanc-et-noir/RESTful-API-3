package com.spring.api.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil{
    private final RedisTemplate<String, String> redisTemplate;
    
    @Autowired
	RedisUtil(RedisTemplate redisTemplate){
		this.redisTemplate = redisTemplate;
	}
	
	public void setData(String key, String value, long time) {
		if(key!=null&&value!=null&&time>0) {
			redisTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
		}
	}
	
	public String getData(String key) {
		if(key!=null) {
			return redisTemplate.opsForValue().get(key);
		}else {
			return null;
		}
	}
	
	public void delete(String key) {
		if(key!=null) {
			redisTemplate.delete(key);
		}
	}
}