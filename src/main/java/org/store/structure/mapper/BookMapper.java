package org.store.structure.mapper;

import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.book.CreateBookRequestDto;
import org.store.structure.model.Book;
import org.store.structure.model.Category;
import org.store.structure.repository.book.BookRepository;

@Mapper(config = MapperConfig.class,
        uses = BookRepository.class
)
public interface BookMapper {
    @Mapping(target = "categoryIds", ignore = true)
    BookDto toDto(Book book);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "id", ignore = true)
    Book toEntity(CreateBookRequestDto dto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(book.getCategories()
                .stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
    }
}
