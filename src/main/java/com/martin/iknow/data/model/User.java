package com.martin.iknow.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "average_score")
    private Integer averageScore;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private List<Quiz> createdQuizzed;

    @OneToMany(mappedBy = "user")
    private List<Attempt> attempts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String toString(){
        return "<username: " + username + "; password " + password + ">";
    }


    public boolean removeAttempt(Attempt attempt){
        return attempts.remove(attempt);
    }

    public Attempt getPendingAttempt(Long quizId){
        return attempts.stream()
                .filter(a -> !a.getIsFinished() && a.getQuiz().getId().equals(quizId))
                .findFirst().orElse(null);
    }
}
