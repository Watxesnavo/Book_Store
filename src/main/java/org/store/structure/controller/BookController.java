package org.store.structure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.dto.book.CreateBookRequestDto;
import org.store.structure.service.book.BookService;

@Tag(name = "Book management", description = "Endpoints to manage books")
@RestController
@RequestMapping(value = "/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Show all the books", description = "Show all the books from DB")
    public List<BookDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a new book", description = "Create new book")
    public ResponseEntity<BookDto> createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.save(requestDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by its id", description = "get a book by id")
    public ResponseEntity<BookDtoWithoutCategoryIds> getBookById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(bookService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a book", description = "updates a book by id")
    public ResponseEntity<BookDto> updateBook(
            @PathVariable Long id, @RequestBody @Valid CreateBookRequestDto bookDto
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(id, bookDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete the book",
            description = "mark a book in BD as deleted (soft deleted)")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "search a book with parameters",
            description = "criteria search for a book")
    public ResponseEntity<List<BookDto>> searchBooks(BookSearchParametersDto searchParameters) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(bookService.searchBooks(searchParameters));
    }
}
