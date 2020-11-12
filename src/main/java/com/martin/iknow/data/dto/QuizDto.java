package com.martin.iknow.data.dto;

import com.martin.iknow.data.model.Quiz;
import com.martin.iknow.data.model.Theme;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuizDto {
    private Long id;
    private String name;
    private String description;
    private Boolean finished;
    private Date createdAt;
    private Long createdByUserId;
    private List<ThemeDto> themes;
    private Integer likes;
    private Integer dislikes;

    public QuizDto(Quiz quiz){
        id = quiz.getId();
        name = quiz.getName();
        description = quiz.getDescription();
        createdAt = quiz.getCreatedAt();
        createdByUserId = quiz.getCreatedBy().getId();
        finished = quiz.getFinished();
        themes = quiz.getThemes().stream().map(ThemeDto::new).collect(Collectors.toList());

        likes = quiz.getUsersLiked().size();
        dislikes = quiz.getUsersDisliked().size();
    }
}
