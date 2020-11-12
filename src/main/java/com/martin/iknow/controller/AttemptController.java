package com.martin.iknow.controller;

import com.martin.iknow.data.repository.AttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(path = "/api/attempts", produces = "application/json")
@CrossOrigin
public class AttemptController {

    private final AttemptRepository attemptRepository;

    @Autowired
    public AttemptController(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }
}
