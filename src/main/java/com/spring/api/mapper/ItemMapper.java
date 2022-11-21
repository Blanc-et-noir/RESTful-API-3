package com.spring.api.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.entity.CommentEntity;
import com.spring.api.entity.ItemEntity;

@Mapper
public interface ItemMapper {
	public ItemEntity readItemByItemId(HashMap param);
	public int readNewItemId();
	public void createItem(HashMap param);
	public void createItemImages(HashMap param);
	public List<ItemEntity> readItems(HashMap param);
	public void createHashtags(HashMap param);
	public void createCommentContent(HashMap<String, String> param);
	public CommentEntity readCommentByCommentId(HashMap<String, String> param);
	public void deleteComment(HashMap<String, String> param);
}