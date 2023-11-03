package org.store.structure.repository.shopping_cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}
