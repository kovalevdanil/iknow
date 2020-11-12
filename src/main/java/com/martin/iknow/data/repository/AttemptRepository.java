package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Attempt;
import com.martin.iknow.data.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttemptRepository extends PagingAndSortingRepository<Attempt, Long> {
    Integer countByUser(User user);

    @Query(value = "select count(a) from attempts a where user_id = :id", nativeQuery = true)
    Integer countByUserId(Long id);

    @Query(value = "select * from attempts where user_id = :userId and is_finished = false limit 1", nativeQuery = true)
    Optional<Attempt> findPendingAttempt(@Param("userId") Long userId);
}
