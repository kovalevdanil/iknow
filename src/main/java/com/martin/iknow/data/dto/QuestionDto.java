package com.martin.iknow.data.dto;

import com.martin.iknow.data.model.Question;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class QuestionDto {

    private Long id;
    private String content;
    private Integer number;

    private List<AnswerDto> answers;

    public QuestionDto(Question question){
        id = question.getId();
        content = question.getContent();
        number = question.getNumber();

        answers = question.getAnswers()
                .stream().map(AnswerDto::new)
                .collect(Collectors.toList());
    }
}
