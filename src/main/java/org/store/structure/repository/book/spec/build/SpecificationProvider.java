package org.store.structure.repository.book.spec.build;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    Specification<T> getSpecification(String[] params);

    String getKey();
}
