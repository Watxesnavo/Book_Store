package org.store.structure.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.book.CreateBookRequestDto;
import org.store.structure.dto.category.CategoryRequestDto;
import org.store.structure.dto.category.CategoryResponseDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithUserDetails(value = "vs@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
class CategoryControllerTest {
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
                    new ClassPathResource("database/bookscategories/add-category-to-"
                            + "book-into-categories_books-table.sql")
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
                    new ClassPathResource("database/users/delete-admin-user.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/delete-user.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/roles/delete-roles.sql"));
        }
    }

    @SneakyThrows
    @Test
    void createCategory_ValidRequestDto_ReturnsDto() {
        CategoryResponseDto expected = new CategoryResponseDto()
                .setDescription("testingHere")
                .setName("testHere")
                .setId(2L);

        CategoryRequestDto category = new CategoryRequestDto()
                .setName("testHere")
                .setDescription("testingHere");
        MvcResult result = mockMVC.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class
        );
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void getAll_Success_ReturnDtoList() {
        CategoryResponseDto cat1 = new CategoryResponseDto()
                .setDescription("testing")
                .setName("test")
                .setId(1L);
        CategoryResponseDto cat2 = new CategoryResponseDto()
                .setDescription("testingHere")
                .setName("testHere")
                .setId(2L);
        List<CategoryResponseDto> expected = List.of(cat1, cat2);
        MvcResult result = mockMVC.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        List<CategoryResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
    }

    @SneakyThrows
    @Test
    void getCategoryById_ValidId_ReturnsDto() {
        CategoryResponseDto expected = new CategoryResponseDto()
                .setDescription("testing")
                .setName("test")
                .setId(1L);
        MvcResult result = mockMVC.perform(get("/categories/{id}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CategoryResponseDto.class);

        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @Sql(scripts = {
            "classpath:database/categories/restore-category.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_ValidIdAndRequestDto_ReturnsDto() {
        CategoryResponseDto expected = new CategoryResponseDto()
                .setDescription("testingA")
                .setName("testA")
                .setId(1L);
        CategoryRequestDto request = new CategoryRequestDto()
                .setName("testA")
                .setDescription("testingA");
        MvcResult result = mockMVC.perform(put("/categories/{id}", 1)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class
        );
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void deleteCategory_ValidId_Success() {
        mockMVC.perform(delete("/categories/{id}", 1))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getBooksByCategoryId_ValidId_ReturnsDtoList() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("CumViatsa")
                .setIsbn("123")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(25.99))
                .setDescription("testing")
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L));
        BookDtoWithoutCategoryIds responseDto = new BookDtoWithoutCategoryIds()
                .setAuthor(requestDto.getAuthor())
                .setTitle(requestDto.getTitle())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setId(1L);
        List<BookDtoWithoutCategoryIds> expected = List.of(responseDto);

        MvcResult result = mockMVC.perform(get("/categories/{id}/books", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0), actual.get(0));
    }
}
