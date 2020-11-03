package com.martin.iknow.data.representation;

import com.martin.iknow.controller.UserController;
import com.martin.iknow.data.model.User;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class UserRepresentationModelAssembler extends RepresentationModelAssemblerSupport<User, UserRepresentationModel> {

    public UserRepresentationModelAssembler(){
        super(UserController.class, UserRepresentationModel.class);
    }

    @Override
    public UserRepresentationModel instantiateModel(User user){
        return new UserRepresentationModel(user);
    }

    @Override
    public UserRepresentationModel toModel(User entity) {
        return createModelWithId(entity.getId(), entity);
    }
}
