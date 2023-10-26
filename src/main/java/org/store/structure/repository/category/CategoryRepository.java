package org.store.structure.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
