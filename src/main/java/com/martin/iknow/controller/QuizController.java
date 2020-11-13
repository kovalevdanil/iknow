package com.martin.iknow.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.martin.iknow.data.QuizOrderByField;
import com.martin.iknow.data.dto.QuestionDto;
import com.martin.iknow.data.dto.QuizDto;
import com.martin.iknow.data.dto.ThemeDto;
import com.martin.iknow.data.form.AnswerForm;
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
public class QuizController extends BaseController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final QuizRepository quizRepository;
    private final AttemptRepository attemptRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ThemeRepository themeRepository;
    private final UserAnswerRepository userAnswerRepository;


    @Autowired
    public QuizController(QuizRepository quizRepository, AttemptRepository attemptRepository, AnswerRepository answerRepository, UserRepository userRepository, QuestionRepository questionRepository, ThemeRepository themeRepository, UserAnswerRepository userAnswerRepository) {

        super(userRepository);

        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.themeRepository = themeRepository;
        this.userAnswerRepository = userAnswerRepository;
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
        User user = getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);

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
    public ResponseEntity<JSONObject> postQuizStart(@AuthenticationPrincipal String username,
                                                    @PathVariable(name = "id") Long id) {
        User user = getUserByUsername(username);
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

    @PostMapping("{id}/terminate")
    public ResponseEntity<JSONObject> postQuizTerminate(@AuthenticationPrincipal String username,
                                                        @PathVariable(name = "id") Long id) {
        User user = getUserByUsername(username);
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

    @GetMapping("{id}/questions")
    public ResponseEntity<?> getQuizQuestions(@AuthenticationPrincipal String username,
                                                              @PathVariable(name = "id") Long id){
        User user = getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Attempt currentAttempt = getPendingAttemptForUser(user);
        if (currentAttempt == null){
            JSONObject response = new JSONObject();
            response.put("message", "you must start quiz first");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Quiz currentQuiz = currentAttempt.getQuiz();

        if (!currentQuiz.getId().equals(id)){
            JSONObject response = new JSONObject();
            response.put("message", "you have pending attempt");
            response.put("quizId", currentAttempt.getQuiz().getId());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        List<Question> questions = currentQuiz.getQuestions();
        List<QuestionDto> questionsRepresentation = questions.stream().map(QuestionDto::new).collect(Collectors.toList());

        return new ResponseEntity<>(questionsRepresentation, HttpStatus.OK);
    }


    @PostMapping("{id}/answer")
    public ResponseEntity<?> saveAnswer(@AuthenticationPrincipal String username,
                                        @RequestBody AnswerForm form,
                                        @PathVariable(name = "id") Long id) {
        User user = getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (form.getAnswerIds() == null || form.getAnswerIds().size() == 0 || form.getQuestionId() == null){
            JSONObject response = new JSONObject();
            response.put("message", "invalid data format");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Attempt currentAttempt = getPendingAttemptForUser(user);
        if (currentAttempt == null){
            JSONObject response = new JSONObject();
            response.put("message", "you should start quiz first");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Quiz quiz = currentAttempt.getQuiz();
        if (!quiz.getId().equals(id)){
            JSONObject response = new JSONObject();
            response.put("message", "you have unfinished attempt. terminate it first");
            response.put("quizId", quiz.getId());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Question question = quiz.getQuestions().stream().filter(q -> q.getId().equals(id)).findFirst().orElse(null);
        if (question == null){
            JSONObject response = new JSONObject();
            response.put("message", "invalid question id");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<Answer> answers = question.getAnswers();

        List<Answer> chosenAnswers = answers.stream().filter(answer ->
            form.getAnswerIds().stream().anyMatch(answerId -> answer.getId().equals(answerId))
        ).collect(Collectors.toList());

        if (chosenAnswers.size() != form.getAnswerIds().size()){
            JSONObject response = new JSONObject();
            response.put("message", "invalid answer id(s)");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setQuestion(question);
        userAnswer.setAttempt(currentAttempt);
        userAnswer.setAnswer(chosenAnswers);

        userAnswerRepository.save(userAnswer);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("{id}/like")
    public ResponseEntity<?> postQuizLike(@AuthenticationPrincipal String username,
                                          @PathVariable(name = "id") Long id) {
        User user = getUserByUsername(username);
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

        User user = getUserByUsername(username);
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

        User user = getUserByUsername(username);
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

        User user = getUserByUsername(username);
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
        User user = getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);


        if (!action.equals("like") && !action.equals("dislike")){

            JSONObject response = new JSONObject();
            response.put("error_message", "incorrect action. valid actions are: 'like', 'dislike'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (quiz.userAssessed(user)){
            JSONObject response = new JSONObject();
            response.put("message", "rate is set");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if (action.equals("like")){
            quiz.addUserLike(user);
        }
        else { // dislike
            quiz.addUserDislike(user);
        }

        quizRepository.save(quiz);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // супер сомнительный метод
    @DeleteMapping("{id}/rate")
    public ResponseEntity<?> deleteQuizRate(@PathVariable(name = "id") Long id,
                                            @RequestParam(name = "action") String action,
                                            @AuthenticationPrincipal String username){
        User user = getUserByUsername(username);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        if (!action.equals("like") && !action.equals("dislike")){
            JSONObject response = new JSONObject();
            response.put("error_message", "incorrect action. valid actions are: 'like', 'dislike'");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (action.equals("like") && quiz.deleteUserLiked(user) || action.equals("dislike") && quiz.deleteUserDisliked(user)  ) {
            quizRepository.save(quiz);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        JSONObject response = new JSONObject();
        response.put("message", "rate wasn't set");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }


    private Attempt getPendingAttemptForUser(User user){
        return user.getAttempts().stream().filter(a -> !a.getIsFinished()).findFirst().orElse(null);
    }

}
