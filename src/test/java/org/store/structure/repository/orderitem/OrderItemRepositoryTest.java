package org.store.structure.repository.orderitem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.store.structure.model.OrderItem;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderItemRepositoryTest {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Sql(scripts = {
            "classpath:database/categories/add-category-to-categories-table.sql",
            "classpath:database/books/add-book-to-books-table.sql",
            "classpath:database/bookscategories/add-category-to-book-into-categories_books-table.sql",
            "classpath:database/users/add-user-into-users-table.sql",
            "classpath:database/orders/add-order-into-orders-table.sql",
            "classpath:database/orderitems/add-item-to-order_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/bookscategories/delete-categories-from-books.sql",
            "classpath:database/books/delete-book.sql",
            "classpath:database/categories/delete-category.sql",
            "classpath:database/users/delete-user.sql",
            "classpath:database/orders/delete-order.sql",
            "classpath:database/orderitems/delete-item.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findAllByOrderUserEmail_WithValidEmail_ReturnsItemSet() {
        Set<OrderItem> orderItems = orderItemRepository.findAllByOrderUserEmail("vs@gmail.com");
        assertEquals(1, orderItems.size());
    }

    @Sql(scripts = {
            "classpath:database/categories/add-category-to-categories-table.sql",
            "classpath:database/books/add-book-to-books-table.sql",
            "classpath:database/bookscategories/add-category-to-book-into-categories_books-table.sql",
            "classpath:database/users/add-user-into-users-table.sql",
            "classpath:database/orders/add-order-into-orders-table.sql",
            "classpath:database/orderitems/add-item-to-order_items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/bookscategories/delete-categories-from-books.sql",
            "classpath:database/books/delete-book.sql",
            "classpath:database/categories/delete-category.sql",
            "classpath:database/users/delete-user.sql",
            "classpath:database/orders/delete-order.sql",
            "classpath:database/orderitems/delete-item.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findByOrderIdAndId_WithValidOrderIdAndItemId_ReturnsItem() {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findByOrderIdAndId(1L, 1L);
        assertEquals("CumViatsa", optionalOrderItem.get().getBook().getTitle());
        assertEquals(1, optionalOrderItem.get().getQuantity());
    }
}