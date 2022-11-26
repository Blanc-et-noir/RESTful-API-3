package com.spring.api.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.spring.api.dto.ItemWithItemImageDTO;
import com.spring.api.entity.CommentEntity;
import com.spring.api.entity.ItemEntity;
import com.spring.api.entity.ItemImageEntity;

@Mapper
public interface ItemMapper {
	public ItemWithItemImageDTO readItemByItemId(HashMap param);
	public int readNewItemId();
	public void createItem(HashMap param);
	public void createItemImages(HashMap param);
	public List<ItemWithItemImageDTO> readItems(HashMap param);
	public void createHashtags(HashMap param);
	public void createCommentContent(HashMap<String, String> param);
	public CommentEntity readCommentByCommentId(HashMap<String, String> param);
	public void deleteComment(HashMap<String, String> param);
	public List<CommentEntity> readComments(HashMap param);
	public void createReplyCommentContent(HashMap<String, String> param);
	public void updateComment(HashMap<String, String> param);
	public ItemImageEntity readItemImageByItemImageId(HashMap<String, String> param);
}