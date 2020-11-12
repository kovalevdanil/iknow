package com.martin.iknow.data.dto;

import com.martin.iknow.data.model.Theme;
import lombok.Data;

@Data
public class ThemeDto {
    private Long id;
    private String name;

    public ThemeDto(Theme theme){
        id = theme.getId();
        name = theme.getName();
    }
}
