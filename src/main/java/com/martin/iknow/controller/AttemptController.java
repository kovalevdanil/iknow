package com.martin.iknow.controller;

import com.martin.iknow.data.model.Attempt;
import com.martin.iknow.data.repository.AttemptRepository;
import com.martin.iknow.data.representation.AttemptRepresentationModel;
import com.martin.iknow.data.representation.AttemptRepresentationModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


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
