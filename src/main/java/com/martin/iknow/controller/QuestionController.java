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

    @GetMapping
    public CollectionModel<QuestionRepresentationModel> getQuestions(@RequestParam(name = "page") Integer page,
                                                                    @RequestParam(name = "size") Integer size){
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;

        List<Question> questions = questionRepository.findAll(PageRequest.of(page, size, Sort.by("id"))).getContent();

        CollectionModel<QuestionRepresentationModel> models = new QuestionRepresentationModelAssembler().toCollectionModel(questions);
        models.add(WebMvcLinkBuilder.linkTo(QuestionController.class).withRel("questions"));

        return models;
    }

    @GetMapping("{id}")
    public ResponseEntity<QuestionRepresentationModel> getQuestion(@PathVariable("id") Long id){

        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new QuestionRepresentationModel(question.get()), HttpStatus.OK);
    }
}
