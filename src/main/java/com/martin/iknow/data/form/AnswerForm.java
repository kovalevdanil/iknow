package com.martin.iknow.data.form;

import lombok.Data;

import java.util.List;

@Data
public class AnswerForm {
    private Long questionId;
    private List<Long> answerIds;
}
