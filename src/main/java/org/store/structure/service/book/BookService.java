package org.store.structure.service.book;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.dto.book.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto updateBook(Long bookId, CreateBookRequestDto bookDto);

    void deleteById(Long id);

    List<BookDto> searchBooks(BookSearchParametersDto searchParameters);

}
