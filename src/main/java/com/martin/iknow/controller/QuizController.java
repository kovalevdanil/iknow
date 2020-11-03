package com.martin.iknow.controller;

import com.martin.iknow.data.model.Quiz;
import com.martin.iknow.data.repository.QuizRepository;
import com.martin.iknow.data.representation.QuizRepresentationModel;
import com.martin.iknow.data.representation.QuizRepresentationModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping(path = "/api/quizzes", produces = {"application/json"})
@CrossOrigin
public class QuizController {

    private QuizRepository quizRepository;

    @Autowired
    public QuizController(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @GetMapping
    public CollectionModel<QuizRepresentationModel> getQuizzes(@RequestParam("page") Integer page,
                                                               @RequestParam("size") Integer size){
        if (page == null)
            page = 0;
        if (size == 0)
            page = 10;

        List<Quiz> quizzes = quizRepository
                .findAll(PageRequest.of(page, size, Sort.by("rating").descending())).getContent();

        CollectionModel<QuizRepresentationModel> models = new QuizRepresentationModelAssembler().toCollectionModel(quizzes);

        models.add(WebMvcLinkBuilder.linkTo(QuizController.class).withRel("quizzes"));

        return models;
    }

    @GetMapping("{id}")
    public ResponseEntity<QuizRepresentationModel> getQuiz(@PathVariable(name = "id") Long id){
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new QuizRepresentationModel(quiz), HttpStatus.OK);
    }

}
