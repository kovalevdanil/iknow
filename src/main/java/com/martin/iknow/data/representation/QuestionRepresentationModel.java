package com.martin.iknow.data.representation;

import com.martin.iknow.data.model.Question;
import lombok.Getter;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.Column;
import java.util.List;

@Relation(value = "question", collectionRelation = "questions")
public class QuestionRepresentationModel extends RepresentationModel<QuestionRepresentationModel> {

    @Getter
    private final String content;
    @Getter
    private final String points;

    @Getter
    private final CollectionModel<AnswerRepresentationModel> answers;

    public QuestionRepresentationModel(Question entity){
        content = entity.getContent();
        points = entity.getPoints();

        AnswerRepresentationModelAssembler answerAssembler = new AnswerRepresentationModelAssembler();
        answers = answerAssembler.toCollectionModel(entity.getAnswers());
    }
}
