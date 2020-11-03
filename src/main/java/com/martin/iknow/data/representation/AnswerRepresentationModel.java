package com.martin.iknow.data.representation;

import com.martin.iknow.data.model.Answer;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "answer", collectionRelation = "answers")
public class AnswerRepresentationModel extends RepresentationModel<AnswerRepresentationModel> {
    @Getter
    private String content;

    @Getter
    private final Boolean isCorrect;

    public AnswerRepresentationModel(Answer entity){
        content = entity.getContent();
        isCorrect = entity.getIsCorrect();
    }
}
