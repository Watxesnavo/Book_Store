package org.store.structure.repository.orderitem;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Set<OrderItem> findAllByOrderUserEmail(String email);
}
