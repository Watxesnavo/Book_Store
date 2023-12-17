package org.store.structure.repository.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.store.structure.model.Order;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Sql(scripts = {
            "classpath:database/users/add-user-into-users-table.sql",
            "classpath:database/orders/add-order-into-orders-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/orders/delete-order.sql",
            "classpath:database/users/delete-user.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findAllByUserEmail_WithValidEmail_ReturnsOrderList() {
        List<Order> orders = orderRepository.findAllByUserEmail("vs@gmail.com");
        assertEquals(1, orders.get(0).getUser().getId());
        assertEquals(1, orders.size());
    }
}
