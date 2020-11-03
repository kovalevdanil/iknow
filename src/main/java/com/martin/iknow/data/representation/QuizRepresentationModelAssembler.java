package com.martin.iknow.data.representation;

import com.martin.iknow.controller.QuizController;
import com.martin.iknow.data.model.Quiz;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class QuizRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Quiz, QuizRepresentationModel > {

    public QuizRepresentationModelAssembler() {
        super(QuizController.class, QuizRepresentationModel.class);
    }

    @Override
    public QuizRepresentationModel instantiateModel(Quiz entity){
        return new QuizRepresentationModel(entity);
    }

    @Override
    public QuizRepresentationModel toModel(Quiz entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
