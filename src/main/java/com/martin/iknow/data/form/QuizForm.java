package com.martin.iknow.data.form;

import com.martin.iknow.data.model.Quiz;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;

@Data
public class QuizForm {
    private String name;
    private String description;

    public Quiz toQuiz(){
        Quiz quiz = new Quiz();
        quiz.setName(name);
        quiz.setDescription(description);
        quiz.setCreatedAt(new Date());
        quiz.setFinished(false);

        quiz.setQuestions(new ArrayList<>());
        quiz.setThemes(new ArrayList<>());
        quiz.setUsersDisliked(new ArrayList<>());
        quiz.setUsersLiked(new ArrayList<>());


        return quiz;
    }
}
