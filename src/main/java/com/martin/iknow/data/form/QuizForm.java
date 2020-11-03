package com.martin.iknow.data.form;

import com.martin.iknow.data.model.Quiz;
import lombok.Data;

@Data
public class QuizForm {
    private String name;
    private String description;

    public Quiz toQuiz(){
        Quiz quiz = new Quiz();
        quiz.setName(name);
        quiz.setDescription(description);
        quiz.setRating((byte) 0);

        return quiz;
    }
}
