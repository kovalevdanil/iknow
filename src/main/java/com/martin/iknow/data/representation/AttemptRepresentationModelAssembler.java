package com.martin.iknow.data.representation;

import com.martin.iknow.controller.AttemptController;
import com.martin.iknow.data.model.Attempt;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class AttemptRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Attempt, AttemptRepresentationModel> {

    public AttemptRepresentationModelAssembler() {
        super(AttemptController.class, AttemptRepresentationModel.class);
    }

    @Override
    public AttemptRepresentationModel instantiateModel(Attempt entity){
        return new AttemptRepresentationModel(entity);
    }

    @Override
    public AttemptRepresentationModel toModel(Attempt entity) {
        return null;
    }
}
