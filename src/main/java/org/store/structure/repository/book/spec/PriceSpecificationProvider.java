package org.store.structure.repository.book.spec;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.store.structure.model.Book;
import org.store.structure.repository.SpecificationProvider;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get("price")
                .in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return "price";
    }
}
