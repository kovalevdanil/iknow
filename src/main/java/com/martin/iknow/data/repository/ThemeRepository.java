package com.martin.iknow.data.repository;

import com.martin.iknow.data.model.Theme;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ThemeRepository extends PagingAndSortingRepository<Theme, Long> {
}
