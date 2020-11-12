package com.martin.iknow.data.dto;

import com.martin.iknow.data.model.User;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;

    public UserDto(User user){
        id = user.getId();
        username = user.getUsername();
    }
}
