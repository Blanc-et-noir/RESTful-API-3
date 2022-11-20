package com.spring.api.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.entity.ItemEntity;

@Mapper
public interface ItemMapper {
	public int readNewItemId();
	public void createItem(HashMap param);
	public void createItemImages(HashMap param);
	public List<ItemEntity> readItems(HashMap param);
}