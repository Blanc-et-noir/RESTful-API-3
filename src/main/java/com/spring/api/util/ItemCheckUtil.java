package com.spring.api.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.spring.api.code.ItemError;
import com.spring.api.exception.CustomException;

@Component
public class ItemCheckUtil {
	private RegexUtil regexUtil;
	private HashMap<String,Boolean> extensions;
	
	@Autowired
	ItemCheckUtil(RegexUtil regexUtil){
		this.regexUtil = regexUtil;
		this.extensions = new HashMap<String,Boolean>();
		
		extensions.put("image/jpeg", true);
		extensions.put("image/gif", true);
		extensions.put("image/png", true);
	}
	
	public void checkItemName(String item_name) {
		if(!regexUtil.checkBytes(item_name, regexUtil.getITEM_NAME_MAX_BYTES())) {
			throw new CustomException(ItemError.ITEM_NAME_EXCEED_MAX_BYTES);
		}
	}

	public void checkItemDescription(String item_description) {
		if(!regexUtil.checkBytes(item_description, regexUtil.getITEM_DESCRIPTION_MAX_BYTES())) {
			throw new CustomException(ItemError.ITEM_DESCRIPTION_EXCEED_MAX_BYTES);
		}
	}

	public void checkItemPrice(String item_price) {
		try {
			int num = Integer.parseInt(item_price);
			
			if(num<1||num>100000000) {
				throw new CustomException(ItemError.ITEM_PRICE_OUT_OF_RANGE);
			}
		}catch(Exception e) {
			throw new CustomException(ItemError.ITEM_PRICE_NOT_MATCHED_TO_REGEX);
		}		
	}

	public void checkItemNumber(String item_number) {
		try {
			int num = Integer.parseInt(item_number);
			
			if(num<1||num>100000000) {
				throw new CustomException(ItemError.ITEM_NUMBER_OUT_OF_RANGE);
			}
		}catch(Exception e) {
			throw new CustomException(ItemError.ITEM_NUMBER_NOT_MATCHED_TO_REGEX);
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
}