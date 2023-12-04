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
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.category.CategoryRequestDto;
import org.store.structure.dto.category.CategoryResponseDto;
import org.store.structure.service.book.BookService;
import org.store.structure.service.category.CategoryService;

@Tag(name = "Category management", description = "Endpoints to manage categories")
@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a new category", description = "Create new category")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody @Valid CategoryRequestDto categoryDto
    ) {
        log.info("create Category method started");
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(categoryDto));
    }

    @GetMapping
    @Operation(summary = "Show all the categories", description = "Show all the categories from DB")
    public ResponseEntity<List<CategoryResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.FOUND).body(categoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show category by its id", description = "Show category by its id from DB")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update category", description = "updates a category by id")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id,
                                              @RequestBody @Valid CategoryRequestDto categoryDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(id, categoryDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete the category",
            description = "mark a category in BD as deleted (soft deleted)")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get books by category's id",
            description = "show books by it's category's id")
    public ResponseEntity<List<BookDtoWithoutCategoryIds>> getBooksByCategoryId(
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.FOUND).body(bookService.findAllByCategoryId(id));
    }
}
