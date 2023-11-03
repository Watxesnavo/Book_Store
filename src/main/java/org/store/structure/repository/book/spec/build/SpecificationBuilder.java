package org.store.structure.repository.book.spec.build;

import org.springframework.data.jpa.domain.Specification;
import org.store.structure.dto.book.BookSearchParametersDto;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParametersDto searchParameters);
}
