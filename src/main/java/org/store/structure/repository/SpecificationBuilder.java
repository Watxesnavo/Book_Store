package org.store.structure.repository;

import org.springframework.data.jpa.domain.Specification;
import org.store.structure.dto.BookSearchParametersDto;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto searchParameters);
}
