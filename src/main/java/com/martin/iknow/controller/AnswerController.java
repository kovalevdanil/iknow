package com.martin.iknow.controller;

import com.martin.iknow.data.model.Answer;
import com.martin.iknow.data.model.Attempt;
import com.martin.iknow.data.repository.AnswerRepository;
import com.martin.iknow.data.repository.AttemptRepository;
import com.martin.iknow.data.representation.AnswerRepresentationModel;
import com.martin.iknow.data.representation.AnswerRepresentationModelAssembler;
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
@RequestMapping(path = "/api/answers", produces = "application/json")
@CrossOrigin
public class AnswerController {

    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerController(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @GetMapping
    public CollectionModel<AnswerRepresentationModel> getAnswers(@RequestParam(name = "page") Integer page,
                                                                   @RequestParam(name = "size") Integer size){
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;

        List<Answer> answers =  answerRepository.findAll(PageRequest.of(page, size, Sort.by("id"))).getContent();
        CollectionModel<AnswerRepresentationModel> models = new AnswerRepresentationModelAssembler()
                .toCollectionModel(answers);

        models.add(WebMvcLinkBuilder.linkTo(AnswerController.class).withRel("answers"));

        return models;
    }

    @GetMapping("{id}")
    public ResponseEntity<AnswerRepresentationModel> getAttempt(@PathVariable(name ="id") Long id){
        Optional<Answer> answer = answerRepository.findById(id);
        if (answer.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new AnswerRepresentationModel(answer.get()), HttpStatus.OK);
    }
}
