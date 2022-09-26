package com.spring.api.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper {
	public HashMap findUserByIdAndPw(HashMap param);
}