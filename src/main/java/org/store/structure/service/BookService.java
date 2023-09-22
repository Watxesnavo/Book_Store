package org.store.structure.service;

import java.util.List;
import org.store.structure.dto.BookDto;
import org.store.structure.dto.BookSearchParametersDto;
import org.store.structure.dto.CreateBookRequestDto;
import org.store.structure.model.Book;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto updateBook(Long bookId, Book newBook);

    void deleteById(Long id);

    List<BookDto> searchBooks(BookSearchParametersDto searchParameters);

}
