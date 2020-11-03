package com.martin.iknow.controller;

import com.martin.iknow.data.representation.UserRepresentationModel;
import com.martin.iknow.data.form.SignupForm;
import com.martin.iknow.data.model.User;
import com.martin.iknow.data.repository.UserRepository;
import com.martin.iknow.data.representation.UserRepresentationModelAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/users", produces = "application/json")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Integer defaultPageSize = 10;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public CollectionModel<UserRepresentationModel> getUsers(@RequestParam(required = false, name = "page") Integer page,
                                                             @RequestParam(required = false, name = "size") Integer size) {
        if (page == null)
            page = 0;
        if (size == null)
            size = defaultPageSize;

        List<User> users = userRepository.findAll(PageRequest.of(page, size, Sort.by("id").ascending())).getContent();

        UserRepresentationModelAssembler assembler = new UserRepresentationModelAssembler();
        CollectionModel<UserRepresentationModel> models = assembler.toCollectionModel(users);

        Link link = WebMvcLinkBuilder
                .linkTo(UserController.class)
                        .withRel("users");
        models.add(link);

        return models;
    }

    @GetMapping("{id}")
    public ResponseEntity<UserRepresentationModel> getUser(@PathVariable("id") Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new UserRepresentationModelAssembler().toModel(user), HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRepresentationModel signupUser(@Valid @RequestBody SignupForm form){
        return new UserRepresentationModel(userRepository.save(form.toUser(passwordEncoder)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id){
        try {
            userRepository.deleteById(id);
        }catch (EmptyResultDataAccessException ignored) {}
    }



}
