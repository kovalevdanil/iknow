package com.martin.iknow.controller;

import com.martin.iknow.data.QuizOrderByField;
import com.martin.iknow.data.dto.QuizDto;
import com.martin.iknow.data.dto.ThemeDto;
import com.martin.iknow.data.form.QuizForm;
import com.martin.iknow.data.model.*;
import com.martin.iknow.data.repository.*;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/quizzes", produces = {"application/json"})
@CrossOrigin
public class QuizController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final QuizRepository quizRepository;
    private final AttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ThemeRepository themeRepository;


    @Autowired
    public QuizController(QuizRepository quizRepository, AttemptRepository attemptRepository, AnswerRepository answerRepository, UserRepository userRepository, QuestionRepository questionRepository, ThemeRepository themeRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.themeRepository = themeRepository;
    }

    // TODO check if page or size < 0, fix orderBy
    @GetMapping
    public ResponseEntity<List<QuizDto>> getQuizzes(@RequestParam(value = "page", required = false) Optional<Integer> pageParam,
                                                    @RequestParam(value = "size", required = false) Optional<Integer> sizeParam,
                                                    @RequestParam(value = "orderBy", required = false) Optional<String> orderByParam,
                                                    @RequestParam(value = "themes", required = false) Optional<String> themesParam) {
        Integer page = pageParam.orElse(0);
        Integer size = sizeParam.orElse(DEFAULT_PAGE_SIZE);
        String orderBy = orderByParam.orElse(QuizOrderByField.CREATED_AT.name().toLowerCase());
        List<String> themes = themesParam.map(value -> Arrays.asList(value.split(","))).orElse(Collections.emptyList());

        if (!QuizOrderByField.validFieldName(orderBy))
            return ResponseEntity.badRequest().build();

        List<Quiz> quizList = quizRepository.findAll(PageRequest.of(page, size)).getContent(); // Sort.by(orderBy)

        if (themes.size() > 0)
            quizList = quizList.stream().filter(quiz ->
                themes.stream().anyMatch(s -> quiz.getThemes().stream().anyMatch(theme -> theme.getName().equals(s)))
            ).collect(Collectors.toList());

        List<QuizDto> response = quizList.stream().map(QuizDto::new).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO model validation
    @PostMapping
    public ResponseEntity<QuizDto> postQuiz(@AuthenticationPrincipal String username, @RequestBody QuizForm form) {
        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);

        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        Quiz quiz = form.toQuiz();
        quiz.setCreatedBy(user);

        quizRepository.save(quiz);

        return new ResponseEntity<>(new QuizDto(quiz), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable(name = "id") Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(new QuizDto(quiz), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteQuiz(@AuthenticationPrincipal String username, @PathVariable(name = "id") Long id) {
        if (username == null)
            return new ResponseEntity<>( HttpStatus.UNAUTHORIZED);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!quiz.getCreatedBy().getUsername().equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        for (var question : quiz.getQuestions()) {
            answerRepository.deleteAll(question.getAnswers());
            questionRepository.delete(question);
        }
        quizRepository.delete(quiz);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}/themes")
    public ResponseEntity<List<ThemeDto>> getQuizThemes(@PathVariable(name = "id") Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        var themes = quiz.getThemes().stream().map(ThemeDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(themes);
    }

    @PostMapping("{id}/themes")
    public ResponseEntity<?> postThemeToQuiz(@AuthenticationPrincipal String username,
                                          @PathVariable(name = "id") Long id,
                                          @RequestParam(name = "themeId") Long themeId) {
        if (username == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return ResponseEntity.notFound().build();

        if (!quiz.getCreatedBy().getUsername().equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Theme theme = themeRepository.findById(themeId).orElse(null);

        if (theme == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (quiz.getThemes().stream().anyMatch(t -> t.getId().equals(theme.getId())))
            return new ResponseEntity<>(HttpStatus.OK);

        quiz.addTheme(theme);
        quizRepository.save(quiz);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}/themes")
    public ResponseEntity<?> deleteThemeFromQuiz(@AuthenticationPrincipal String username,
                                              @PathVariable(name = "id") Long id,
                                              @RequestParam(name = "themeId") Long themeId) {
        if (username == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return ResponseEntity.notFound().build();

        if (!quiz.getCreatedBy().getUsername().equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Theme theme = themeRepository.findById(themeId).orElse(null);

        if (theme == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (quiz.deleteTheme(theme))
            quizRepository.save(quiz);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("{id}/start")
    public ResponseEntity<JSONObject> postStartQuiz(@AuthenticationPrincipal String username,
                                                    @PathVariable(name = "id") Long id) {
        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        if (user.getAttempts().stream().anyMatch(a -> !a.getIsFinished())) {
            JSONObject response = new JSONObject();
            response.put("error_message", "unable to start new attempt while unfinished one exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        Attempt attempt = new Attempt();
        attempt.setIsFinished(false);
        attempt.setStartedAt(new Date());
        attempt.setQuiz(quiz);
        attempt.setUser(user);

        attemptRepository.save(attempt);

        JSONObject response = new JSONObject();
        response.put("message", "new attempt was started successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/terminate")
    public ResponseEntity<JSONObject> postQuizStop(@AuthenticationPrincipal String username,
                                                   @PathVariable(name = "id") Long id) {
        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        Attempt unfinishedAttempt = user.getPendingAttempt(id);

        if (unfinishedAttempt == null) {
            JSONObject response = new JSONObject();
            response.put("message", "Quiz wasn't started or doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        user.removeAttempt(unfinishedAttempt);

        attemptRepository.delete(unfinishedAttempt);

        JSONObject response = new JSONObject();
        response.put("message", "Attempt was successfully terminated");

        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }


    @PostMapping("{id}")
    public ResponseEntity<?> saveAnswer(@AuthenticationPrincipal User user,
                                        @RequestBody Long answerId, @RequestBody Long questionId) {
        Optional<Attempt> currentAttemptOptional = attemptRepository.findPendingAttempt(user.getId());
        if (currentAttemptOptional.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        Attempt currentAttempt = currentAttemptOptional.get();
        Quiz currentQuiz = currentAttempt.getQuiz();
        Question question = currentQuiz.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId)).findAny().orElse(null);

        if (question == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        Answer answer = question.getAnswers().stream().filter(a -> a.getId().equals(answerId)).findAny().orElse(null);

        if (answer == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        UserAnswer answerAttempt = new UserAnswer();
        answerAttempt.setAnswer(Collections.singletonList(answer));
        answerAttempt.setAttempt(currentAttempt);
        answerAttempt.setQuestion(question);

        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/like")
    public ResponseEntity<?> postQuizLike(@AuthenticationPrincipal String username,
                                          @PathVariable(name = "id") Long id) {
        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        if (quiz.userAssessed(user)) {
            JSONObject response = new JSONObject();
            response.put("error_message", "you've already assessed this quiz");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        quiz.addUserLike(user);
        quizRepository.save(quiz);

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/like")
    public ResponseEntity<?> deleteQuizLike(@AuthenticationPrincipal String username,
                                            @PathVariable(name = "id") Long id) {

        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        if (!quiz.deleteUserLiked(user)) {
            JSONObject response = new JSONObject();
            response.put("message", "like doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        quizRepository.save(quiz);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("{id}/dislike")
    public ResponseEntity<?> postQuizDislike(@AuthenticationPrincipal String username,
                                             @PathVariable(name = "id") Long id) {
        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        if (quiz.userAssessed(user)) {
            JSONObject response = new JSONObject();
            response.put("error_message", "you've already assessed this quiz");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // Сомнительный результат. Forbidden же к возможности доступа относится
        }

        quiz.addUserLike(user);
        quizRepository.save(quiz);

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/dislike")
    public ResponseEntity<?> deleteQuizDislike(@AuthenticationPrincipal String username,
                                               @PathVariable(name = "id") Long id) {

        if (username == null)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        if (!quiz.deleteUserDisliked(user)) {
            JSONObject response = new JSONObject();
            response.put("message", "dislike doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }

        quizRepository.save(quiz);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("{id}/rate")
    public ResponseEntity<?> postQuizRate(@PathVariable(name = "id") Long id,
                                          @RequestParam(name = "action") String action,
                                          @AuthenticationPrincipal String username){
        if (!action.equals("like") && !action.equals("dislike"))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // TODO replacement for {id}/like {id}/dislike

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
