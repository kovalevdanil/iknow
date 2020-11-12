package com.martin.iknow.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONObject;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.ManyToAny;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import java.util.Date;
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

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "created_at")
    @ColumnDefault("now()")
    private Date createdAt;

    @Column(name = "finished")
    @ColumnDefault("false")
    private Boolean finished;

    @OneToMany(targetEntity = Question.class)
    @JoinColumn(name = "quiz_id")
    @OrderBy(value = "number asc")
    private List<Question> questions;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany
    @JoinTable(name = "theme_quiz",
                joinColumns = {@JoinColumn(name = "quiz_id")},
                inverseJoinColumns = {@JoinColumn(name = "theme_id")})
    private List<Theme> themes;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "user_like",
                joinColumns = {@JoinColumn(name = "quiz_id")},
                inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersLiked;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "user_dislike",
                joinColumns = {@JoinColumn(name = "quiz_id")},
                inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> usersDisliked;


    public void addTheme(Theme theme){
        themes.add(theme);
    }

    public boolean deleteTheme(Theme theme) {
        return themes.remove(theme);
    }

    public void addUserLike(User user) {
        usersLiked.add(user);
    }

    public boolean deleteUserLiked(User user) {
        return usersLiked.remove(user);
    }

    public boolean userAssessed(User user) {
        return usersDisliked.stream().anyMatch(u -> u.getId().equals(user.getId())) ||
               usersLiked.stream().anyMatch(u -> u.getId().equals(user.getId()));
    }

    public boolean deleteUserDisliked(User user) {
        return usersDisliked.remove(user);
    }
}
