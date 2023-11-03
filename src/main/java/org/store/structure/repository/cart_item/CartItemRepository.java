package org.store.structure.repository.cart_item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
