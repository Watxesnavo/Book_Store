package org.store.structure.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Transactional
    @Query(value = "DELETE FROM books_categories WHERE book_id = :id", nativeQuery = true)
    void removeCategoriesByBookId(Long id);
}
