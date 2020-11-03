package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Question;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long > {
}
