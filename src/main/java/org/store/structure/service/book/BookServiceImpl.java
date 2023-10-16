package org.store.structure.service.book;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.dto.book.CreateBookRequestDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.BookMapper;
import org.store.structure.model.Book;
import org.store.structure.model.Category;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.book.BookSpecificationBuilder;
import org.store.structure.repository.category.CategoryRepository;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toEntity(requestDto);
        getCategoriesByIds(requestDto.getCategoryIds())
                .forEach(category -> category.addBook(book));
        book.setCategories(getCategoriesByIds(requestDto.getCategoryIds()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    private Set<Category> getCategoriesByIds(Collection<Long> ids) {
        return ids.stream()
                .map(categoryRepository::findById)
                .map(category -> category.orElseThrow(
                        () -> new EntityNotFoundException("Can't find category " + category)))
                .collect(Collectors.toSet());
    }

    @Override
    public BookDtoWithoutCategoryIds findById(Long id) {
        return bookMapper.toDtoWithoutCategories(bookRepository.findById(id).orElseThrow(
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
            categoryRepository.removeCategoriesByBookId(bookId);
            getCategoriesByIds(newBook.getCategoryIds())
                    .forEach(category -> category.addBook(book));
            book.setCategories(getCategoriesByIds(newBook.getCategoryIds()));
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

    @Override
    public List<BookDto> findAllByCategoryId(Long categoryId) {
        return bookRepository.findAllByCategoryId(categoryId).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
