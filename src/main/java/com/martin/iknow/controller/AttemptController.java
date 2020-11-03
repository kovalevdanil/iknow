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

    @GetMapping
    public CollectionModel<AttemptRepresentationModel> getAttempts(@RequestParam(name = "page") Integer page,
                                                                   @RequestParam(name = "size") Integer size){
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;

        List<Attempt> attempts = attemptRepository.findAll(PageRequest.of(page, size, Sort.by("id"))).getContent();
        CollectionModel<AttemptRepresentationModel> models = new AttemptRepresentationModelAssembler()
                                                                        .toCollectionModel(attempts);

        models.add(WebMvcLinkBuilder.linkTo(AttemptController.class).withRel("attempts"));

        return models;
    }

    @GetMapping("{id}")
    public ResponseEntity<AttemptRepresentationModel> getAttempt(@PathVariable(name ="id") Long id){
        Optional<Attempt> attempt = attemptRepository.findById(id);
        if (!attempt.isPresent())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new AttemptRepresentationModel(attempt.get()), HttpStatus.OK);
    }
}
