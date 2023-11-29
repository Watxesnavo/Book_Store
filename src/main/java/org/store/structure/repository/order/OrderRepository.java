package org.store.structure.repository.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.store.structure.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserEmail(String email);
}
