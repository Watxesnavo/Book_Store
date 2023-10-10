package org.store.structure.repository.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.model.Book;
import org.store.structure.repository.SpecificationBuilder;
import org.store.structure.repository.SpecificationProviderManager;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.prices() != null && searchParameters.prices().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("price")
                    .getSpecification(searchParameters.prices()));
        }
        if (searchParameters.coverImages() != null && searchParameters.coverImages().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider("coverImage")
                    .getSpecification(searchParameters.coverImages()));
        }
        return spec;
    }
}
