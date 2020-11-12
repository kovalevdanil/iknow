package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Answer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends PagingAndSortingRepository<Answer, Long> {
    
    @Query(value = "select a from answers a where quiz_id = :quizId and is_correct = true", nativeQuery = true)
    List<Answer> findCorrectAnswersByQuizId(Long quizId);

    @Query(value = "select true from answers a join questoins q on a.quiestion_id = q.id and q.quiz_id = :quizId limit 1", nativeQuery = true)
    Boolean isAnswerRelatedToQuiz(@Param("quizId") Long quizId);

    @Query(value = "select true from answers where question_id = :questionId limit 1", nativeQuery = true)
    Boolean isAnswerRelatedToQuestion(@Param("questionId") Long questionId);
}
