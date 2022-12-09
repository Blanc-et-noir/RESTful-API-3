package com.spring.api.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.spring.api.code.ItemError;
import com.spring.api.dto.HashtagDTO;
import com.spring.api.dto.ItemImageDTO;
import com.spring.api.dto.ItemWithItemImagesDTO;
import com.spring.api.entity.CommentEntity;
import com.spring.api.entity.ItemImageEntity;
import com.spring.api.exception.CustomException;
import com.spring.api.mapper.ItemMapper;

@Component
public class ItemCheckUtil {
	private ItemMapper itemMapper;
	private RegexUtil regexUtil;
	private HashMap<String,Boolean> extensions;
	private final long ITEM_FREQUENCY;
	
	@Autowired
	ItemCheckUtil(
		ItemMapper itemMapper, 
		RegexUtil regexUtil,
		@Value("${frequency.item}") long ITEM_FREQUENCY
	){
		this.regexUtil = regexUtil;
		this.itemMapper = itemMapper;
		this.extensions = new HashMap<String,Boolean>();
		this.ITEM_FREQUENCY = ITEM_FREQUENCY;
		
		extensions.put("image/jpeg", true);
		extensions.put("image/gif", true);
		extensions.put("image/png", true);
	}
	
	public void checkItemName(String item_name, boolean isNullable) {
		if(isNullable) {
			return;
		}else if(!regexUtil.checkBytes(item_name, regexUtil.getITEM_NAME_MAX_BYTES())) {
			throw new CustomException(ItemError.ITEM_NAME_EXCEED_MAX_BYTES);
		}
	}

	public void checkItemDescription(String item_description, boolean isNullable) {
		if(isNullable) {
			return;
		}else if(!regexUtil.checkBytes(item_description, regexUtil.getITEM_DESCRIPTION_MAX_BYTES())) {
			throw new CustomException(ItemError.ITEM_DESCRIPTION_EXCEED_MAX_BYTES);
		}
	}

	public void checkItemPrice(String item_price, boolean isNullable) {
		if(isNullable) {
			return;
		}else {
			try {
				int num = Integer.parseInt(item_price);
				
				if(num<1||num>100000000) {
					throw new CustomException(ItemError.ITEM_PRICE_OUT_OF_RANGE);
				}
			}catch(Exception e) {
				throw new CustomException(ItemError.ITEM_PRICE_NOT_MATCHED_TO_REGEX);
			}
		}
	}

	public void checkItemNumber(String item_number, boolean isNullable) {
		if(isNullable) {
			return;
		}else {
			try {
				int num = Integer.parseInt(item_number);
				
				if(num<1||num>100000000) {
					throw new CustomException(ItemError.ITEM_NUMBER_OUT_OF_RANGE);
				}
			}catch(Exception e) {
				throw new CustomException(ItemError.ITEM_NUMBER_NOT_MATCHED_TO_REGEX);
			}
		}	
	}

	public void checkItemImages(List<MultipartFile> item_images) {
		Iterator<MultipartFile> itor = item_images.iterator();
		while(itor.hasNext()) {
			MultipartFile file = itor.next();
			
			String contentType = file.getContentType();
			
			if(!extensions.containsKey(contentType)) {
				itor.remove();
			}
		}
	}
	
	public int checkPageRegex(String page) {
		int num = 0;
		
		if(page==null) {
			return 0;
		}
		
		try {
			 num = Integer.parseInt(page);
		}catch(Exception e) {
			throw new CustomException(ItemError.PAGE_NOT_MATCHED_TO_REGEX);
		}
		
		if(num<0) {
			throw new CustomException(ItemError.PAGE_OUT_OF_RANGE);
		}
		
		return num;
	}

	public int checkLimitRegex(String limit) {
		int val = 0;
		
		if(limit==null) {
			return regexUtil.getMIN_LIMIT();
		}
		
		try {
			val = Integer.parseInt(limit);
		}catch(Exception e) {			
			throw new CustomException(ItemError.LIMIT_NOT_MATCHED_TO_REGEX);
		}
		
		if(!(val>=regexUtil.getMIN_LIMIT()&&val<=regexUtil.getMAX_LIMIT())) {
			throw new CustomException(ItemError.LIMIT_OUT_OF_RANGE);
		}
		
		return val;
	}

	public void checkHashtagContentsRegex(List<String> hashtag_contents) {
		if(hashtag_contents != null) {			
			for(String hashtag_content : hashtag_contents) {
				if(!regexUtil.checkBytes(hashtag_content, regexUtil.getHASHTAG_CONTENT_MAX_BYTES())) {
					throw new CustomException(ItemError.HASHTAG_EXCEED_MAX_BYTES);
				}else if(!regexUtil.checkRegex(hashtag_content, regexUtil.getHASHTAG_CONTENT_REGEX())) {
					throw new CustomException(ItemError.HASHTAG_CONTENT_NOT_MATCHED_TO_REGEX);
				}
			}
		}		
	}

	public ItemWithItemImagesDTO isItemExistent(HashMap param) {
		ItemWithItemImagesDTO itemWithItemImageDTO = null;
		
		if((itemWithItemImageDTO = itemMapper.readItemByItemId(param)) == null) {
			throw new CustomException(ItemError.NOT_FOUND_ITEM);
		}
		
		return itemWithItemImageDTO;
	}

	public void checkItemIdRegex(String item_id) {
		try {
			Integer.parseInt(item_id);	
		}catch(Exception e) {
			throw new CustomException(ItemError.ITEM_ID_NOT_MATCHED_TO_REGEX);
		}		
	}

	public void checkCommentContent(String comment_content) {
		if(!regexUtil.checkBytes(comment_content, regexUtil.getCOMMENT_CONTENT_MAX_BYTES())) {
			throw new CustomException(ItemError.COMMENT_CONTENT_EXCEED_MAX_BYTES);
		}
	}

	public void checkCommentIdRegex(String comment_id) {
		try {
			Integer.parseInt(comment_id);	
		}catch(Exception e) {
			throw new CustomException(ItemError.COMMENT_ID_NOT_MATCHED_TO_REGEX);
		}
	}

	public CommentEntity isCommentExistent(HashMap<String, String> param) {
		CommentEntity commentEntity = null;
		
		if((commentEntity = itemMapper.readCommentByCommentId(param)) == null) {
			throw new CustomException(ItemError.NOT_FOUND_COMMENT);
		}
		
		return commentEntity;
	}

	public void isEditableComment(CommentEntity commentEntity, String user_id) {
		if(!user_id.equals(commentEntity.getUser_id())) {
			throw new CustomException(ItemError.CAN_NOT_DELETE_OR_UPDATE_COMMENT_BY_USER_ID);
		}		
	}

	public void checkItemImageIdRegex(String item_image_id) {
		try {
			Integer.parseInt(item_image_id);	
		}catch(Exception e) {
			throw new CustomException(ItemError.ITEM_IMAGE_ID_NOT_MATCHED_TO_REGEX);
		}	
	}

	public ItemImageDTO isItemImageExistent(ItemWithItemImagesDTO itemWithItemImagesDTO, String item_id) {
		ItemImageDTO itemImageDTO = null;
		
		for(ItemImageDTO itemImage: itemWithItemImagesDTO.getItem_images()) {
			if(itemImage.getItem_id() == Integer.parseInt(item_id)) {
				itemImageDTO = itemImage;
				break;
			}
		}
		
		if(itemImageDTO != null) {
			return itemImageDTO;
		}else {
			throw new CustomException(ItemError.NOT_FOUND_ITEM_IMAGE);
		}
	}

	public void isEditableItem(ItemWithItemImagesDTO itemWithItemImagesDTO, String user_id) {
		if(!user_id.equals(itemWithItemImagesDTO.getUser_id())) {
			throw new CustomException(ItemError.CAN_NOT_DELETE_OR_UPDATE_ITEM_BY_USER_ID);
		}	
	}
	
	public void checkUserItemTime(Timestamp userItemTime) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		if(userItemTime != null && (now.getTime()-userItemTime.getTime())/1000 <= ITEM_FREQUENCY) {
			throw new CustomException(ItemError.TOO_FREQUENT_TO_CREATE_ITEM);
		}
	}

	public void isNotSold(ItemWithItemImagesDTO itemWithItemImagesDTO) {
		if(itemWithItemImagesDTO.getItem_status().equals("S")) {
			throw new CustomException(ItemError.ALREADY_SOLD_ITEM);
		}
	}

	public void areHashtagsExistent(ItemWithItemImagesDTO itemWithItemImagesDTO, List<String> hashtag_ids) {
		HashMap hm = new HashMap();

		if(itemWithItemImagesDTO.getHashtags()!=null) {
			for(HashtagDTO hashtag : itemWithItemImagesDTO.getHashtags()) {
				hm.put(hashtag.getHashtag_id()+"",true);
			}
		}
		
		if(hashtag_ids!=null) {
			for(String hashtag_id : hashtag_ids) {
				if(!hm.containsKey(hashtag_id)) {
					throw new CustomException(ItemError.NOT_FOUND_HASHTAG);
				}
			}
		}
	}

	public void areItemImagesExistent(ItemWithItemImagesDTO itemWithItemImagesDTO, List<String> item_image_ids) {
		HashMap hm = new HashMap();

		if(itemWithItemImagesDTO.getItem_images()!=null) {
			for(ItemImageDTO itemImage : itemWithItemImagesDTO.getItem_images()) {
				hm.put(itemImage.getItem_image_id()+"",true);
			}	
		}
		
		if(item_image_ids!=null) {
			for(String item_image_id : item_image_ids) {
				if(!hm.containsKey(item_image_id)) {
					throw new CustomException(ItemError.NOT_FOUND_ITEM_IMAGE);
				}
			}
		}
	}

	public void checkHashtagIdsRegex(List<String> hashtag_ids) {
		if(hashtag_ids!=null) {
			for(String hashtag_id : hashtag_ids) {
				try {
					int val = Integer.parseInt(hashtag_id);
				}catch(Exception e) {
					throw new CustomException(ItemError.HASHTAG_ID_NOT_MATCHED_TO_REGEX);
				}
			}
		}
	}

	public void checkItemImageIdsRegex(List<String> item_image_ids) {
		if(item_image_ids!=null) {
			for(String item_image_id : item_image_ids) {
				try {
					int val = Integer.parseInt(item_image_id);
				}catch(Exception e) {
					throw new CustomException(ItemError.ITEM_IMAGE_ID_NOT_MATCHED_TO_REGEX);
				}
			}
		}
	}
}