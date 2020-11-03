package com.martin.iknow.data.representation;

import com.martin.iknow.data.model.Quiz;
import lombok.Getter;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Relation(value = "quiz", collectionRelation = "quizzes")
public class QuizRepresentationModel extends RepresentationModel<QuizRepresentationModel> {
    private final String name;
    private final String description;
    private final Byte rating;
    private CollectionModel<ThemeRepresentationModel> themes;
    private final CollectionModel<QuestionRepresentationModel> questions;

    public QuizRepresentationModel(Quiz entity){
        name = entity.getName();
        description = entity.getDescription();
        rating = entity.getRating();

        var questionAssembler = new QuestionRepresentationModelAssembler();
        var userAssembler = new UserRepresentationModelAssembler();
        var themeAssembler = new ThemeRepresentationModelAssembler();

        questions = questionAssembler.toCollectionModel(entity.getQuestions());
        themes = themeAssembler.toCollectionModel(entity.getThemes());
    }
}
