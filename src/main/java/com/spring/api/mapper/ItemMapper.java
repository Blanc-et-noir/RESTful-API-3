package com.spring.api.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper {
	public int readNewItemId();
	public void createItem(HashMap param);
	public void createItemImages(HashMap param);
}
