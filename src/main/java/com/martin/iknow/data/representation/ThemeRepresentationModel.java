package com.martin.iknow.data.representation;

import com.martin.iknow.data.model.Theme;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "theme", collectionRelation = "themes")
public class ThemeRepresentationModel extends RepresentationModel<ThemeRepresentationModel> {
    @Getter
    private String name;

    public ThemeRepresentationModel(Theme entity){
        name = entity.getName();
    }
}
