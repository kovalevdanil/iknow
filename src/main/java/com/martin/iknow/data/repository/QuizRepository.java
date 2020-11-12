package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Quiz;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends PagingAndSortingRepository<Quiz, Long> {

    @Query(value = "select q.* from quizzes q join attempts a on q.id = a.quiz_id " +
                   "where a.user_id = :userId and is_finished = false limit 1",
                    nativeQuery = true)
    Optional<Quiz> getPendingQuizForUserId(@Param("userId") Long userId);
}
