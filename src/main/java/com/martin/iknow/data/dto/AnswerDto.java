package com.martin.iknow.data.dto;

import com.martin.iknow.data.model.Answer;
import lombok.Getter;

@Getter
public class AnswerDto {

    private Long id;
    private String content;

    public AnswerDto(Answer answer){
        id = answer.getId();
        content = answer.getContent();
    }
}
