package org.store.structure.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;
    private Book book;
    private Category category;

    @BeforeEach
    void setUp() {
        book = initBook();
        category = initCategory();
        category.setBooks(Set.of(book));
        book.setCategories(Set.of(category));
    }

    @Test
    @DisplayName("Check the correct functionality of save method")
    void save_WithValidRequestDto_ReturnsBookDto() {
        CreateBookRequestDto bookRequestDto = initCreationRequestDto(book);
        bookRequestDto.setCategoryIds(Set.of(category.getId()));
        BookDto expected = initBookDto(book);

        when(bookMapper.toDto(book)).thenReturn(expected);
        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(categoryRepository.findAllById(bookRequestDto.getCategoryIds()))
                .thenReturn(List.of(category));
        when(bookRepository.save(book)).thenReturn(book);

        BookDto actual = bookService.save(bookRequestDto);
        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper, categoryRepository);
    }

    @Test
    @DisplayName("Check correct findById method response")
    void findById_WithValidId_ReturnsValidBookDto() {
        BookDtoWithoutCategoryIds expected = initBookDtoWithOutCategories(book);

        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(expected);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        BookDtoWithoutCategoryIds actual = bookService.findById(book.getId());

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Check incorrect findById method request")
    void findById_WithInvalidId_ThrowsException() {
        Long bookId = 2L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class, () -> bookService.findById(bookId)
        );
        String expected = "Can't find book by id: " + bookId;
        assertEquals(expected, entityNotFoundException.getMessage());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Check findAll method response")
    void findAll_ValidPageable_ReturnAllBooks() {
        BookDto bookDto = initBookDto(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> bookDtoList = bookService.findAll(pageable);

        int expectedSize = 1;
        assertEquals(expectedSize, bookDtoList.size());
        assertEquals(bookDtoList.get(0), bookDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Check valid update method response")
    void updateBook_WithValidIdAndRequestDto_ReturnsBookDto() {
        BookDto bookDto = initBookDto(book);
        CreateBookRequestDto bookRequestDto = initCreationRequestDto(book);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(categoryRepository.findAllById(bookRequestDto.getCategoryIds()))
                .thenReturn(List.of(category));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actual = bookService.updateBook(book.getId(), bookRequestDto);

        assertEquals(bookDto, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper, categoryRepository);
    }

    @Test
    @DisplayName("Check Invalid update method response")
    void updateBook_WithInvalidCategoryId_ThrowsException() {
        CreateBookRequestDto bookRequestDto = initCreationRequestDto(book);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(categoryRepository.findAllById(bookRequestDto.getCategoryIds()))
                .thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateBook(book.getId(), bookRequestDto)
        );
        String expected = "Can't get categories by this ids " + bookRequestDto.getCategoryIds();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(bookRepository, bookMapper, categoryRepository);
    }

    @Test
    @DisplayName("Check invalid update method response")
    void updateBook_WithInvalidBookId_ThrowsException() {
        CreateBookRequestDto bookRequestDto = initCreationRequestDto(book);
        long bookId = 2L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException actual = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateBook(bookId, bookRequestDto)
        );
        String expected = "Can't find book by id: " + bookId;
        assertEquals(expected, actual.getMessage());
        verifyNoMoreInteractions(bookRepository, bookMapper, categoryRepository);
    }

    @Test
    @DisplayName("Check correct response of search method by author parameter")
    void searchBooks_WithValidAuthorSearchParameter_ReturnsLisOfBookDto() {
        BookDto bookDto = initBookDto(book);
        final List<BookDto> expected = List.of(bookDto);
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                new String[]{book.getAuthor()},
                new String[]{null},
                new String[]{null},
                new String[]{null}
        );

        when(bookSpecificationBuilder.build(searchParametersDto))
                .thenReturn(
                        (root, query, criteriaBuilder) -> root.get("author").in(
                                Arrays.stream(searchParametersDto.authors()).toArray()
                        )
                );
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        when(bookMapper.toDto(any())).thenReturn(bookDto);
        when(bookRepository.findAll(bookSpecification)).thenReturn(List.of(book));

        List<BookDto> actual = bookService.searchBooks(searchParametersDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper, bookSpecificationBuilder);
    }

    @Test
    @DisplayName("Check correct response of search method by title parameter")
    void searchBooks_WithValidTitleSearchParameter_ReturnsLisOfBookDto() {
        BookDto bookDto = initBookDto(book);
        final List<BookDto> expected = List.of(bookDto);
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                new String[]{null},
                new String[]{book.getTitle()},
                new String[]{null},
                new String[]{null}
        );

        when(bookSpecificationBuilder.build(searchParametersDto))
                .thenReturn(
                        (root, query, criteriaBuilder) -> root.get("title").in(
                                Arrays.stream(searchParametersDto.titles()).toArray()
                        )
                );
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        when(bookMapper.toDto(any())).thenReturn(bookDto);
        when(bookRepository.findAll(bookSpecification)).thenReturn(List.of(book));

        List<BookDto> actual = bookService.searchBooks(searchParametersDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper, bookSpecificationBuilder);
    }

    @Test
    @DisplayName("Check correct response of search method by price parameter")
    void searchBooks_WithValidPriceSearchParameter_ReturnsLisOfBookDto() {
        BookDto bookDto = initBookDto(book);
        final List<BookDto> expected = List.of(bookDto);
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                new String[]{null},
                new String[]{null},
                new String[]{book.getPrice().toString()},
                new String[]{null}
        );

        when(bookSpecificationBuilder.build(searchParametersDto))
                .thenReturn(
                        (root, query, criteriaBuilder) -> root.get("price").in(
                                Arrays.stream(searchParametersDto.prices()).toArray()
                        )
                );
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        when(bookMapper.toDto(any())).thenReturn(bookDto);
        when(bookRepository.findAll(bookSpecification)).thenReturn(List.of(book));

        List<BookDto> actual = bookService.searchBooks(searchParametersDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper, bookSpecificationBuilder);
    }

    @Test
    @DisplayName("Check correct response of search method by cover image parameter")
    void searchBooks_WithValidCoverImageSearchParameter_ReturnsLisOfBookDto() {
        BookDto bookDto = initBookDto(book);
        final List<BookDto> expected = List.of(bookDto);
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                new String[]{null},
                new String[]{null},
                new String[]{null},
                new String[]{book.getCoverImage()}
        );

        when(bookSpecificationBuilder.build(searchParametersDto))
                .thenReturn(
                        (root, query, criteriaBuilder) -> root.get("coverImage").in(
                                Arrays.stream(searchParametersDto.coverImages()).toArray()
                        )
                );
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParametersDto);
        when(bookMapper.toDto(any())).thenReturn(bookDto);
        when(bookRepository.findAll(bookSpecification)).thenReturn(List.of(book));

        List<BookDto> actual = bookService.searchBooks(searchParametersDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper, bookSpecificationBuilder);
    }

    @Test
    @DisplayName("Check valid findAllByCategoryId method response")
    void findAllByCategoryId_WithValidCategoryId_ReturnsListOfBookDto() {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = initBookDtoWithOutCategories(book);

        when(bookRepository.findAllByCategoriesId(category.getId())).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> expected = List.of(bookDtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(category.getId());

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private Book initBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Vyacheslav");
        book.setDescription("stories of vyacheslav");
        book.setTitle("My Story");
        book.setPrice(BigDecimal.valueOf(9.99));
        book.setIsbn("12345");
        book.setCoverImage("Test.jpg");
        return book;
    }

    private Category initCategory() {
        Category category = new Category();
        category.setName("Biography");
        category.setDescription("Personal stories");
        category.setId(1L);
        return category;
    }

    private CreateBookRequestDto initCreationRequestDto(Book book) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor(book.getAuthor());
        requestDto.setTitle(book.getTitle());
        requestDto.setDescription(book.getDescription());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(book.getPrice());
        requestDto.setCoverImage(book.getCoverImage());
        requestDto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
        return requestDto;
    }

    private BookDtoWithoutCategoryIds initBookDtoWithOutCategories(Book book) {
        BookDtoWithoutCategoryIds requestDto = new BookDtoWithoutCategoryIds();
        requestDto.setAuthor(book.getAuthor());
        requestDto.setTitle(book.getTitle());
        requestDto.setDescription(book.getDescription());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(book.getPrice());
        requestDto.setCoverImage(book.getCoverImage());
        return requestDto;
    }

    private BookDto initBookDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setAuthor(book.getAuthor());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
        dto.setPrice(book.getPrice());
        dto.setCoverImage(book.getCoverImage());
        return dto;
    }
}
