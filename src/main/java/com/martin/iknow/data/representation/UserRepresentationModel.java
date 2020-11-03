package com.martin.iknow.data.representation;

import com.martin.iknow.data.model.User;
import lombok.Getter;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "user", collectionRelation = "users")
public class UserRepresentationModel extends RepresentationModel<UserRepresentationModel> {
    @Getter
    private String username;

    @Getter
    private CollectionModel<QuizRepresentationModel> createdQuizzes;

    public UserRepresentationModel(User user){
        username = user.getUsername();

        var quizAssembler = new QuizRepresentationModelAssembler();
        createdQuizzes = quizAssembler.toCollectionModel(user.getCreatedQuizzed());
    }
}
