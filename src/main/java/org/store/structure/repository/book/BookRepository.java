package org.store.structure.repository.book;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;  
import org.springframework.data.jpa.repository.Query;
import org.store.structure.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query(value = "SELECT * FROM books b INNER JOIN books_categories bc ON "
            + "b.id = bc.book_id WHERE bc.category_id = :categoryId", nativeQuery = true)
    List<Book> findAllByCategoryId(Long categoryId);
}
