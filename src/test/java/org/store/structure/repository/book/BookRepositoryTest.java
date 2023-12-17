package org.store.structure.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.store.structure.model.Book;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
    }

    @Sql(scripts = {
            "classpath:database/categories/add-category-to-categories-table.sql",
            "classpath:database/books/add-book-to-books-table.sql",
            "classpath:database/bookscategories/add-category-to-"
                    + "book-into-categories_books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/bookscategories/delete-categories-from-books.sql",
            "classpath:database/books/delete-book.sql",
            "classpath:database/categories/delete-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findAllByCategoriesId_validCategoryId_ReturnsBookList() {
        List<Book> actual = bookRepository.findAllByCategoriesId(1L);
        assertEquals(1, actual.size());
        assertEquals("CumViatsa", actual.get(0).getTitle());
    }
}
