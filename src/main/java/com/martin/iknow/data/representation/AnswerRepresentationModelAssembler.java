package com.martin.iknow.data.representation;

import com.martin.iknow.controller.AnswerController;
import com.martin.iknow.data.model.Answer;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class AnswerRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Answer, AnswerRepresentationModel> {

    public AnswerRepresentationModelAssembler() {
        super(AnswerController.class, AnswerRepresentationModel.class);
    }

    @Override
    public AnswerRepresentationModel instantiateModel(Answer entity){
        return new AnswerRepresentationModel(entity);
    }

    @Override
    public AnswerRepresentationModel toModel(Answer entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
