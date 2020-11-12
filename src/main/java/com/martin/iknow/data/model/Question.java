package com.martin.iknow.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number")
    private Integer number;

    @Column(name = "content")
    private String content;

    @Column(name = "points")
    private String points;

    @OneToMany(targetEntity = Answer.class)
    @JoinColumn(name = "question_id", nullable = false)
    List<Answer> answers;
}
