package com.martin.iknow.data.representation;

import com.martin.iknow.data.model.Attempt;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.Column;
import java.util.Date;

@Relation(value = "attempt", collectionRelation = "attempts")
public class AttemptRepresentationModel extends RepresentationModel<AttemptRepresentationModel> {
    @Getter
    private final Date startedAt;
    @Getter
    private final Date finishedAt;
    @Getter
    private final Integer pointsScored;
    @Getter
    private final Boolean isFinished;

    @Getter
    private final UserRepresentationModel user;

    @Getter
    private final QuizRepresentationModel quiz;

    public AttemptRepresentationModel(Attempt entity){
        startedAt = entity.getStartedAt();
        finishedAt = entity.getFinishedAt();
        pointsScored = entity.getPointsScored();
        isFinished = entity.getIsFinished();

        var quizAssembler = new QuizRepresentationModelAssembler();
        var userAssembler = new UserRepresentationModelAssembler();

        quiz = quizAssembler.instantiateModel(entity.getQuiz());
        user = userAssembler.instantiateModel(entity.getUser());
    }

}
