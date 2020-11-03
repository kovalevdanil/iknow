package com.martin.iknow.data.representation;

import com.martin.iknow.controller.ThemeController;
import com.martin.iknow.data.model.Theme;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class ThemeRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Theme,ThemeRepresentationModel > {

    public ThemeRepresentationModelAssembler(){
        super(ThemeController.class, ThemeRepresentationModel.class);
    }

    @Override
    public ThemeRepresentationModel instantiateModel(Theme entity){
        return new ThemeRepresentationModel(entity);
    }


    @Override
    public ThemeRepresentationModel toModel(Theme entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
