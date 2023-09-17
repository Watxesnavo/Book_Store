package org.store.structure.service;

import java.util.List;
import org.store.structure.dto.BookDto;
import org.store.structure.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
