package org.store.structure.service.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.dto.book.CreateBookRequestDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.BookMapper;
import org.store.structure.model.Book;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.book.BookSpecificationBuilder;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id: " + id)));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public BookDto updateBook(Long bookId, CreateBookRequestDto newBook) {
        if (bookRepository.findById(bookId).isPresent()) {
            Book book = bookRepository.findById(bookId).get();
            book.setTitle(newBook.getTitle());
            book.setDescription(newBook.getDescription());
            book.setAuthor(newBook.getAuthor());
            book.setCoverImage(newBook.getCoverImage());
            book.setIsbn(newBook.getIsbn());
            book.setPrice(newBook.getPrice());
            return bookMapper.toDto(book);
        } else {
            throw new EntityNotFoundException("book was not found with this id: " + bookId);
        }
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters) {
        return bookRepository.findAll(bookSpecificationBuilder.build(searchParameters))
                .stream()
                .map(bookMapper::toDto).toList();
    }
}
