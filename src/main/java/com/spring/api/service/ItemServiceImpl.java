package com.spring.api.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.api.dto.CommentDTO;
import com.spring.api.dto.ItemWithItemImagesDTO;
import com.spring.api.dto.ItemWithItemThumbnailImageDTO;
import com.spring.api.entity.CommentEntity;
import com.spring.api.entity.ItemImageEntity;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.ItemMapper;
import com.spring.api.util.ItemCheckUtil;

@Service("itemService")
@Transactional
public class ItemServiceImpl implements ItemService{
	private JwtTokenProvider jwtTokenProvider;
	private ItemCheckUtil itemCheckUtil;
	private ItemMapper itemMapper;
	private final String SEP = File.separator;
	private final String BASE_DIRECTORY_OF_IMAGE_FILES = "C:"+SEP+"georaesangeo"+SEP+"items"+SEP+"images"+SEP;
	
	@Autowired
	ItemServiceImpl(JwtTokenProvider jwtTokenProvider,ItemMapper itemMapper, ItemCheckUtil itemCheckUtil){
		this.jwtTokenProvider = jwtTokenProvider;
		this.itemMapper = itemMapper;
		this.itemCheckUtil = itemCheckUtil;
	}
	
	@Override
	public void createItem(MultipartRequest multipartRequest, HttpServletRequest request) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String item_name = request.getParameter("item_name");
		String item_description = request.getParameter("item_description");
		String item_price = request.getParameter("item_price");
		String item_number = request.getParameter("item_number");
		
		List<MultipartFile> item_images = multipartRequest.getFiles("item_images");
		List<String> hashtags = null;
		
		if(request.getParameterValues("hashtags")!=null) {
			hashtags = Arrays.asList(request.getParameterValues("hashtags"));
		}
		
		itemCheckUtil.checkItemName(item_name);
		itemCheckUtil.checkItemDescription(item_description);
		itemCheckUtil.checkItemPrice(item_price);
		itemCheckUtil.checkItemNumber(item_number);
		itemCheckUtil.checkItemImages(item_images);
		itemCheckUtil.checkHashtagsRegex(hashtags);

		int item_id = itemMapper.readNewItemId();
		
		HashMap param = new HashMap();
		param.put("item_id", item_id);
		param.put("item_name", item_name);
		param.put("item_description", item_description);
		param.put("item_price", item_price);
		param.put("item_number", item_number);
		param.put("user_id", user_id);
		param.put("hashtags", hashtags);
		
		itemCheckUtil.checkUserItemTime(itemMapper.readUserItemTime(param));
		
		itemMapper.createItem(param);
		itemMapper.updateUserItemTime(param);
		
		if(hashtags!=null&&hashtags.size()>0) {
			itemMapper.createHashtags(param);
		}
		
		if(item_images!=null&&item_images.size()>0) {
			List<HashMap> image_files = new LinkedList<HashMap>();
			
			HashMap<String, MultipartFile> mappingTable = new HashMap<String,MultipartFile>();
			
			for(MultipartFile multipartFile : item_images) {
				HashMap image = new HashMap();
				String originalFilename = multipartFile.getOriginalFilename();
				String item_image_extension = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
				String item_image_original_name = originalFilename.substring(0,originalFilename.lastIndexOf("."));
				String item_image_stored_name = UUID.randomUUID().toString();
				
				image.put("item_image_original_name", item_image_original_name);
				image.put("item_image_stored_name", item_image_stored_name);
				image.put("item_image_extension", item_image_extension);
				image.put("item_image_size", multipartFile.getSize());
				image_files.add(image);
				
				mappingTable.put(item_image_stored_name+"."+item_image_extension, multipartFile);
			}
			
			param.put("image_files", image_files);
			
			itemMapper.createItemImages(param);
			
			for(String item_image_name : mappingTable.keySet()) {
				MultipartFile multipartFile = mappingTable.get(item_image_name);
				try {
					File file = new File(BASE_DIRECTORY_OF_IMAGE_FILES+item_image_name);

					if(!file.exists()) {
						file.mkdirs();
					}

					multipartFile.transferTo(file);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<ItemWithItemThumbnailImageDTO> readItems(HttpServletRequest request, HashMap param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		
		if(request.getParameterValues("hashtags")!=null) {
			List<String> hashtags = Arrays.asList(request.getParameterValues("hashtags"));
			itemCheckUtil.checkHashtagsRegex(hashtags);
			param.put("hashtags", hashtags);
		}
		
		int limit = itemCheckUtil.checkLimitRegex((String)param.get("limit"));
		int page = itemCheckUtil.checkPageRegex((String)param.get("page"));

		param.put("limit", limit);
		param.put("page", page);
		param.put("offset", page*limit+"");
		
		return itemMapper.readItems(param);
	}

	@Override
	public void createComment(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String item_id = param.get("item_id");
		String comment_content = param.get("comment_content");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkCommentContent(comment_content);
		itemCheckUtil.isItemExistent(param);

		itemMapper.createCommentContent(param);
	}

	@Override
	public void createReplyComment(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String item_id = param.get("item_id");
		String comment_id = param.get("comment_id");
		String comment_content = param.get("comment_content");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkCommentIdRegex(comment_id);
		itemCheckUtil.checkCommentContent(comment_content);
		itemCheckUtil.isItemExistent(param);
		itemCheckUtil.isCommentExistent(param);
		
		itemMapper.createReplyCommentContent(param);
	}

	@Override
	public void deleteComment(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String item_id = param.get("item_id");
		String comment_id = param.get("comment_id");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkCommentIdRegex(comment_id);
		itemCheckUtil.isItemExistent(param);
		
		CommentEntity commentEntity = itemCheckUtil.isCommentExistent(param);
		itemCheckUtil.isEditableComment(commentEntity, user_id);
		itemMapper.deleteComment(param);

	}

	@Override
	public List<CommentDTO> readComments(HttpServletRequest request, HashMap param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String item_id = (String)param.get("item_id");
		
		int limit = itemCheckUtil.checkLimitRegex((String)param.get("limit"));
		int page = itemCheckUtil.checkPageRegex((String)param.get("page"));
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.isItemExistent(param);

		param.put("limit", limit);
		param.put("page", page);
		param.put("offset", page*limit+"");
		
		List<CommentEntity> list = itemMapper.readComments(param);
		
		List<CommentDTO> comments = new LinkedList<CommentDTO>();
		
		for(CommentEntity commentEntity : list) {
			comments.add(new CommentDTO(commentEntity));
		}
		
		return comments;
	}

	@Override
	public void updateComment(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String item_id = param.get("item_id");
		String comment_id = param.get("comment_id");
		String comment_content = param.get("comment_content");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkCommentIdRegex(comment_id);
		itemCheckUtil.checkCommentContent(comment_content);
		itemCheckUtil.isItemExistent(param);
		
		CommentEntity commentEntity = itemCheckUtil.isCommentExistent(param);
		itemCheckUtil.isEditableComment(commentEntity, user_id);
		
		itemMapper.updateComment(param);
	}

	@Override
	public ResponseEntity<Object> readItemImage(HttpServletRequest request, HttpServletResponse response, HashMap<String,String> param) throws IOException {
		String item_id = param.get("item_id");
		String item_image_id = param.get("item_image_id");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkItemImageIdRegex(item_image_id);
		itemCheckUtil.isItemExistent(param);
		ItemImageEntity itemImageEntity = itemCheckUtil.isItemImageExistent(param);
		
		File file = new File(BASE_DIRECTORY_OF_IMAGE_FILES+itemImageEntity.getItem_image_stored_name()+"."+itemImageEntity.getItem_image_extension());
	
		if(file.exists()) {
			HttpHeaders header = new HttpHeaders();
			header.add("Content-Disposition", "attachment; filename="+itemImageEntity.getItem_image_original_name()+"."+itemImageEntity.getItem_image_extension());
			header.add("Cache-Control", "no-cache");
			header.add("Content-Type", "application/octet-stream");
			
			return new ResponseEntity<Object>(FileUtils.readFileToByteArray(file),header,HttpStatus.OK);
		}else {
			return null;
		}
	}

	@Override
	public void deleteItem(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String item_id = param.get("item_id");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		ItemWithItemImagesDTO itemWithItemImageDTO = itemCheckUtil.isItemExistent(param);
		itemCheckUtil.isEditableItem(itemWithItemImageDTO, user_id);
		
		itemMapper.deleteItem(param);
	}

	@Override
	public ItemWithItemImagesDTO readItem(HttpServletRequest request, HashMap<String,String> param) {
		String item_id = param.get("item_id");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		ItemWithItemImagesDTO itemWithItemImagesDTO = itemCheckUtil.isItemExistent(param);
		
		return itemWithItemImagesDTO;		
	}
}