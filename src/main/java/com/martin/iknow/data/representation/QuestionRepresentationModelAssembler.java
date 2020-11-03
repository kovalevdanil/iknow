package com.martin.iknow.data.representation;

import com.martin.iknow.controller.QuestionController;
import com.martin.iknow.data.model.Question;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class QuestionRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Question, QuestionRepresentationModel> {


    public QuestionRepresentationModelAssembler() {
        super(QuestionController.class, QuestionRepresentationModel.class);
    }

    @Override
    public QuestionRepresentationModel instantiateModel(Question entity){
        return new QuestionRepresentationModel(entity);
    }

    @Override
    public QuestionRepresentationModel toModel(Question entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
