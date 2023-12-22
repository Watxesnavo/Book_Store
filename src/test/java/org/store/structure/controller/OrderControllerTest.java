package org.store.structure.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.model.Order;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithUserDetails(value = "vs@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
class OrderControllerTest {
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
                    new ClassPathResource("database/users/set-user-as-admin.sql")
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
                    new ClassPathResource(
                            "database/cartitems/add-item-to-cartitems-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/orders/add-order-into-orders-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/orderitems/add-item-to-order_items-table.sql")
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
                    new ClassPathResource("database/orderitems/delete-item.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/orders/delete-order.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/delete-admin-user.sql"));
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
    void placeNewOrder_ValidRequestDto_ReturnsResponseDto() {
        OrderRequestDto requestDto = new OrderRequestDto()
                .setShippingAddress("sagunt 39");
        OrderItemResponseDto itemResponseDto = new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(1)
                .setBookId(1L);
        OrderResponseDto expected = new OrderResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setTotal(BigDecimal.valueOf(25.99))
                .setStatus(Order.Status.CREATED)
                .setOrderItems(Set.of(itemResponseDto));
        MvcResult result = mockMVC.perform(post("/orders")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        OrderResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                OrderResponseDto.class
        );
        expected.setOrderDate(actual.getOrderDate());
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void placeNewOrder_failedShippingAddressValidation_test() {
        mockMVC.perform(post("/orders")
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/order/invalid-order-shippingaddress.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getOrderHistory_Success_ReturnsResponseDtoList() {
        OrderItemResponseDto itemResponseDto = new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(1)
                .setBookId(1L);
        OrderResponseDto expectedOrder = new OrderResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setTotal(BigDecimal.valueOf(25.99))
                .setStatus(Order.Status.CREATED)
                .setOrderItems(Set.of(itemResponseDto));
        List<OrderResponseDto> expected = List.of(expectedOrder);

        MvcResult result = mockMVC.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void updateStatus_ValidIdAndRequestDto_ReturnsResponseDto() {
        OrderStatusUpdateDto reqUpdate = new OrderStatusUpdateDto();
        reqUpdate.setStatus("PENDING");
        OrderItemResponseDto itemResponseDto = new OrderItemResponseDto()
                .setId(1L)
                .setQuantity(1)
                .setBookId(1L);
        OrderResponseDto expected = new OrderResponseDto()
                .setId(1L)
                .setUserId(1L)
                .setTotal(BigDecimal.valueOf(25.99))
                .setStatus(Order.Status.PENDING)
                .setOrderItems(Set.of(itemResponseDto));
        MvcResult result = mockMVC.perform(patch("/orders/{id}", 1)
                        .content(objectMapper.writeValueAsString(reqUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        OrderResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                OrderResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @SneakyThrows
    void updateStatus_failedStatusValidation_test() {
        mockMVC.perform(patch("/orders/{id}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/order/invalid-status.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getItemsForOrder_ValidOrderId_ReturnsItemResponseSet() {
        OrderItemResponseDto itemResponse = new OrderItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setQuantity(1);
        Set<OrderItemResponseDto> expected = Set.of(itemResponse);

        MvcResult result = mockMVC.perform(get("/orders/{orderId}/items", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        Set<OrderItemResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void getSpecificItemFromOrder_ValidOrderIdAndItemId_ReturnsResponseDto() {
        OrderItemResponseDto expected = new OrderItemResponseDto()
                .setId(1L)
                .setBookId(1L)
                .setQuantity(1);

        MvcResult result = mockMVC.perform(get("/orders/{orderId}/items/{itemId}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        OrderItemResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                OrderItemResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
