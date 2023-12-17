package org.store.structure.repository.cartitem;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBookId(Long bookId);
}
