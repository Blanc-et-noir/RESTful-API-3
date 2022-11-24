package com.spring.api.dto;

import com.spring.api.entity.QuestionEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDTO {	
	private Integer question_id;
	private String question_content;
	
	public QuestionDTO(QuestionEntity questionEntity){		
		this.question_id = questionEntity.getQuestion_id();
		this.question_content = questionEntity.getQuestion_content();
	}
}
