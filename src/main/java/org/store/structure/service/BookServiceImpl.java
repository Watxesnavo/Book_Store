package org.store.structure.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.store.structure.dto.BookDto;
import org.store.structure.dto.CreateBookRequestDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.BookMapper;
import org.store.structure.model.Book;
import org.store.structure.repository.BookRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(repository.save(book));
    }

    @Override
    public BookDto findById(Long id) {
        return bookMapper.toDto(repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cant find book by id: " + id)));
    }

    @Override
    public List<BookDto> findAll() {
        return repository.findAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
