package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Attempt;
import com.martin.iknow.data.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttemptRepository extends PagingAndSortingRepository<Attempt, Long> {
    Integer countByUser(User user);

    @Query(value = "select count(a) from attempts a where user_id = :id", nativeQuery = true)
    Integer countByUserId(Long id);
}
