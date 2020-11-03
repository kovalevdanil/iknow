package com.martin.iknow.data.form;

import com.martin.iknow.data.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignupForm {

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_-]{5,32}")
    private String username;

    @NotNull
    @Size(min = 4)
    private String password;

    @NotNull
    private String passwordConfirm;

    public User toUser(PasswordEncoder passwordEncoder){
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        return user;
    }
}
