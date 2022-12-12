package com.spring.api.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.spring.api.dto.CommentDTO;
import com.spring.api.dto.ItemImageDTO;
import com.spring.api.dto.ItemWithItemImagesDTO;
import com.spring.api.dto.ItemWithItemThumbnailImageDTO;
import com.spring.api.entity.CommentEntity;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.mapper.ItemMapper;
import com.spring.api.util.ItemCheckUtil;

import net.coobird.thumbnailator.Thumbnails;

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
		
		List<MultipartFile> item_images = multipartRequest.getFiles("item_image");
		List<String> hashtag_contents = null;
		
		if(request.getParameterValues("hashtag_content")!=null) {
			hashtag_contents = Arrays.asList(request.getParameterValues("hashtag_content"));
		}
		
		itemCheckUtil.checkItemName(item_name,false);
		itemCheckUtil.checkItemDescription(item_description,false);
		itemCheckUtil.checkItemPrice(item_price,false);
		itemCheckUtil.checkItemNumber(item_number,false);
		itemCheckUtil.checkItemImages(item_images);
		itemCheckUtil.checkHashtagContentsRegex(hashtag_contents);

		int item_id = itemMapper.readNewItemId();
		
		HashMap param = new HashMap();
		param.put("item_id", item_id);
		param.put("item_name", item_name);
		param.put("item_description", item_description);
		param.put("item_price", item_price);
		param.put("item_number", item_number);
		param.put("user_id", user_id);
		param.put("hashtag_contents", hashtag_contents);
		
		itemCheckUtil.checkUserItemTime(itemMapper.readUserItemTime(param));
		
		itemMapper.createItem(param);
		itemMapper.updateUserItemTime(param);
		
		if(hashtag_contents!=null&&hashtag_contents.size()>0) {
			itemMapper.createHashtags(param);
		}
		
		registerFile(item_images, param);
		
		return;
	}

	@Override
	public List<ItemWithItemThumbnailImageDTO> readItems(HttpServletRequest request, HashMap param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		String flag = (String) param.get("flag");
		String search = (String) param.get("search");
		
		if(request.getParameterValues("hashtag_content")!=null) {
			List<String> hashtag_contents = Arrays.asList(request.getParameterValues("hashtag_content"));
			itemCheckUtil.checkHashtagContentsRegex(hashtag_contents);
			param.put("hashtag_contents", hashtag_contents);
		}
		
		itemCheckUtil.checkFlagRegex(flag);
		param.put("search",itemCheckUtil.checkSearchRegex(flag, search));
		
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
		
		return;
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
		
		return;
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

		return;
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
		
		return;
	}

	@Override
	public void readItemImage(HttpServletRequest request, HttpServletResponse response, HashMap<String,String> param) throws IOException {
		String item_id = param.get("item_id");
		String item_image_id = param.get("item_image_id");
		String item_image_type = param.get("item_image_type");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkItemImageIdRegex(item_image_id);
		itemCheckUtil.checkItemImageType(item_image_type);
		
		ItemWithItemImagesDTO itemWithItemImagesDTO = itemCheckUtil.isItemExistent(param);
		ItemImageDTO itemImageDTO = itemCheckUtil.isItemImageExistent(itemWithItemImagesDTO, item_image_id);
		
		File file = new File(BASE_DIRECTORY_OF_IMAGE_FILES+itemImageDTO.getItem_image_stored_name()+"."+itemImageDTO.getItem_image_extension());
		BufferedImage imageFile = null;
		
		if(item_image_type==null||item_image_type.equals("original")) {
			imageFile = ImageIO.read(file);
		}else {
			imageFile = Thumbnails.of(file).size(100, 100).asBufferedImage();
		}
		
		response.setHeader("Cache-Control", "no-cache");
		
		OutputStream out = response.getOutputStream();
		
		ImageIO.write(imageFile, itemImageDTO.getItem_image_extension(), out);
		
		out.close();
		
		return;
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
		
		return;
	}

	@Override
	public ItemWithItemImagesDTO readItem(HttpServletRequest request, HashMap<String,String> param) {
		String item_id = param.get("item_id");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		ItemWithItemImagesDTO itemWithItemImagesDTO = itemCheckUtil.isItemExistent(param);
		
		return itemWithItemImagesDTO;		
	}

	@Override
	public void sellItem(HttpServletRequest request, HashMap<String,String> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);
		param.put("user_id", user_id);
		
		String item_id = param.get("item_id");
		
		itemCheckUtil.checkItemIdRegex(item_id);
		ItemWithItemImagesDTO itemWithItemImagesDTO = itemCheckUtil.isItemExistent(param);
		itemCheckUtil.isNotSold(itemWithItemImagesDTO);
		itemCheckUtil.isEditableItem(itemWithItemImagesDTO, user_id);
		
		itemMapper.sellItem(param);
		
		return;
	}

	@Override
	public void updateItem(MultipartRequest multipartRequest, HttpServletRequest request, HashMap<String, Object> param) {
		String user_accesstoken = request.getHeader("user_accesstoken");
		String user_id = jwtTokenProvider.getUserIdFromJWT(user_accesstoken);

		String item_name = request.getParameter("item_name");
		String item_description = request.getParameter("item_description");
		String item_price = request.getParameter("item_price");
		String item_number = request.getParameter("item_number");
		String item_id = (String) param.get("item_id");
		
		param.put("item_id", item_id);
		param.put("item_name", item_name);
		param.put("item_description", item_description);
		param.put("item_price", item_price);
		param.put("item_number", item_number);
		param.put("user_id", user_id);
		
		List<MultipartFile> item_images = multipartRequest.getFiles("item_image");
		List<String> hashtag_contents = null;
		List<String> hashtag_ids = null;
		List<String> item_image_ids = null;
		
		if(request.getParameterValues("hashtag_id")!=null) {
			hashtag_ids = Arrays.asList(request.getParameterValues("hashtag_id"));
			param.put("hashtag_ids", hashtag_ids);
		}
		
		if(request.getParameterValues("item_image_id")!=null) {
			item_image_ids = Arrays.asList(request.getParameterValues("item_image_id"));
			param.put("item_image_ids", item_image_ids);
		}
		
		if(request.getParameterValues("hashtag_content")!=null) {
			hashtag_contents = Arrays.asList(request.getParameterValues("hashtag_content"));
			param.put("hashtag_contents", hashtag_contents);
		}
		
		itemCheckUtil.checkItemName(item_name,true);
		itemCheckUtil.checkItemDescription(item_description,true);
		itemCheckUtil.checkItemPrice(item_price,true);
		itemCheckUtil.checkItemNumber(item_number,true);
		itemCheckUtil.checkItemIdRegex(item_id);
		itemCheckUtil.checkHashtagIdsRegex(hashtag_ids);
		itemCheckUtil.checkItemImageIdsRegex(item_image_ids);
		
		ItemWithItemImagesDTO itemWithItemImagesDTO = itemCheckUtil.isItemExistent(param);
		itemCheckUtil.isNotSold(itemWithItemImagesDTO);
		itemCheckUtil.isEditableItem(itemWithItemImagesDTO, user_id);
		itemCheckUtil.areHashtagsExistent(itemWithItemImagesDTO, hashtag_ids);
		itemCheckUtil.areItemImagesExistent(itemWithItemImagesDTO, item_image_ids);

		if(hashtag_ids!=null&&hashtag_ids.size()>0) {
			itemMapper.deleteHashtags(param);
		}

		if(item_image_ids!=null&&item_image_ids.size()>0) {
			itemMapper.deleteItemImages(param);
		}

		if(!(item_name == null&&item_description==null&&item_price==null&&item_number==null)) {
			itemMapper.updateItem(param);
		}

		if(hashtag_contents!=null&&hashtag_contents.size()>0) {
			itemMapper.createHashtags(param);
		}

		registerFile(item_images, param);
		
		return;
	}
	
	private void registerFile(List<MultipartFile> item_images, HashMap param) {
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
		
		return;
	}
}