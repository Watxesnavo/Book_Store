package org.store.structure.mapper;

import org.mapstruct.Mapper;
import org.store.structure.config.MapperConfig;
import org.store.structure.dto.BookDto;
import org.store.structure.dto.CreateBookRequestDto;
import org.store.structure.model.Book;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto dto);
}
