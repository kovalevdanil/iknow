package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Answer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AnswerRepository extends PagingAndSortingRepository<Answer, Long> {
    
    @Query(value = "select a from answers a where quiz_id = :quizId and is_correct = true", nativeQuery = true)
    List<Answer> findCorrectAnswersByQuizId(Long quizId);
}
