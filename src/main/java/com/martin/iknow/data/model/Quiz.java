package com.martin.iknow.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "quizzes")
public class Quiz {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "rating")
    private Byte rating;

    @OneToMany(targetEntity = Question.class)
    @JoinColumn(name = "quiz_id")
    private List<Question> questions;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany
    @JoinTable(name = "theme_quiz",
                joinColumns = {@JoinColumn(name = "quiz_id")},
                inverseJoinColumns = {@JoinColumn(name = "theme_id")})
    private List<Theme> themes;
}
