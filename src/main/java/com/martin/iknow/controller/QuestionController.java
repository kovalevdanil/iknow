package com.martin.iknow.controller;

import com.martin.iknow.data.model.Attempt;
import com.martin.iknow.data.model.Question;
import com.martin.iknow.data.repository.QuestionRepository;
import com.martin.iknow.data.representation.AttemptRepresentationModel;
import com.martin.iknow.data.representation.QuestionRepresentationModel;
import com.martin.iknow.data.representation.QuestionRepresentationModelAssembler;
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
@RequestMapping(path = "/api/questions", produces = "application/json")
@CrossOrigin
public class QuestionController {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

}
