package org.store.structure.repository.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.store.structure.model.ShoppingCart;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Sql(scripts = {
            "classpath:database/users/add-user-into-users-table.sql",
            "classpath:database/shoppingcarts/add-cart-to-shopping_carts-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users/delete-user.sql",
            "classpath:database/shoppingcarts/delete-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findFirstByUserEmail_WithValidEmail_ReturnsCartOptional() {
        Optional<ShoppingCart> optionalShoppingCart =
                shoppingCartRepository.findFirstByUserEmail("vs@gmail.com");
        assertEquals(1, optionalShoppingCart.get().getId());
        assertNotNull(optionalShoppingCart);
    }
}
