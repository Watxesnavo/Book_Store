package org.store.structure.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemResponseDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithUserDetails(value = "vs@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
class ShoppingCartControllerTest {
    protected static MockMvc mockMVC;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource
    ) {
        mockMVC = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/roles/add-roles-into-roles-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-user-into-users-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories/add-category-to-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-book-to-books-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingcarts/add-cart-to-shopping_carts-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cartitems/add-item-to-cartitems-table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        tearDown(dataSource);
    }

    @SneakyThrows
    static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/bookscategories/delete-categories-from-books.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-category.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-book.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/cartitems/delete-items-from-cart_items-table.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/shoppingcarts/delete-cart.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/delete-user.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/roles/delete-roles.sql"));
        }
    }

    @Test
    @SneakyThrows
    void getCurrentShoppingCart_Success_ReturnsCartDto() {
        CartItemResponseDto itemResp = new CartItemResponseDto()
                .setShoppingCartId(1L)
                .setBookId(1L)
                .setQuantity(1)
                .setId(1L)
                .setBookTitle("CumViatsa");
        ShoppingCartResponseDto expected = new ShoppingCartResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItems(Set.of(itemResp));

        MvcResult result = mockMVC.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void addBook_ValidRequest_ReturnsCartDto() {
        CartItemResponseDto itemResp = new CartItemResponseDto()
                .setShoppingCartId(1L)
                .setBookId(1L)
                .setQuantity(3)
                .setId(1L)
                .setBookTitle("CumViatsa");
        CartItemResponseDto itemResp2 = new CartItemResponseDto()
                .setShoppingCartId(1L)
                .setBookId(2L)
                .setQuantity(1)
                .setId(2L)
                .setBookTitle("CumViatsa1");
        final ShoppingCartResponseDto expected = new ShoppingCartResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItems(Set.of(itemResp, itemResp2));

        CartItemRequestDto requestDto = new CartItemRequestDto()
                .setBookId(2L)
                .setQuantity(1);
        MvcResult result = mockMVC.perform(post("/cart")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertNotNull(actual);
        System.out.println(actual);
        System.out.println("------");
        System.out.println(expected);
        assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void addBook_BookIdAndQuantityValidationFail_test() {
        mockMVC.perform(post("/cart")
                        .content(new String(
                                Files.readAllBytes(
                                        new File("src/test/resources/request/"
                                                + "cartitem/invalid-bookId-quantity.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @SneakyThrows
    void updateItem_ValidIdAndDto_ReturnsCartDto() {
        CartItemResponseDto itemResp = new CartItemResponseDto()
                .setShoppingCartId(1L)
                .setBookId(1L)
                .setQuantity(3)
                .setId(1L)
                .setBookTitle("CumViatsa");
        ShoppingCartResponseDto expected = new ShoppingCartResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setCartItems(Set.of(itemResp));
        CartItemUpdateDto req = new CartItemUpdateDto();
        req.setQuantity(3);
        MvcResult result = mockMVC.perform(put("/cart/cart-items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartResponseDto.class);

        assertNotNull(actual);
        assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void updateItem_QuantityValidationFail_test() {
        mockMVC.perform(put("/cart/cart-items/{itemId}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/cartitem/invalid-quantity.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @Sql(scripts = {
            "classpath:database/cartitems/update-items.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteItem_ValidId_ReturnsCartDto() {
        mockMVC.perform(delete("/cart/cart-items/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }
}
