package com.spring.api.dto;

import com.spring.api.entity.QuestionEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDTO {	
	private int question_id;
	private String question_content;
	
	public QuestionDTO(QuestionEntity questionEntity){		
		this.question_id = question_id;
		this.question_content = question_content;
	}
}
