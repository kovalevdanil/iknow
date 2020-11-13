package com.martin.iknow.controller;

import com.martin.iknow.data.model.User;
import com.martin.iknow.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {
    private UserRepository userRepository;

    public BaseController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByUsername(String username){
        if (username == null)
            return null;
        return userRepository.findUserByUsername(username).orElse(null);
    }


}
