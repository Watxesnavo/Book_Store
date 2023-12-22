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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.dto.book.CreateBookRequestDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithUserDetails(value = "vs@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
class BookControllerTest {
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

    @Test
    @SneakyThrows
    void createBook_ValidRequestDto_ReturnsResponse() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Test")
                .setIsbn("1234")
                .setAuthor("Sukhov")
                .setPrice(BigDecimal.valueOf(95.99))
                .setDescription("testing")
                .setCoverImage("coverTest")
                .setCategoryIds(Set.of(1L));

        BookDto expected = new BookDto()
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds())
                .setCoverImage(requestDto.getCoverImage())
                .setId(3L);

        MvcResult result = mockMVC.perform(post("/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void createBook_failedTitleValidation_test() {
        mockMVC.perform(post("/books")
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/invalid-title.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createBook_failedAuthorValidation_test() {
        mockMVC.perform(post("/books")
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/empty-author.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createBook_failedCategoriesValidation_test() {
        mockMVC.perform(post("/books")
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/empty-categories.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createBook_failedIsbnValidation_test() {
        mockMVC.perform(post("/books")
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/empty-isbn.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void createBook_failedPriceValidation_test() {
        mockMVC.perform(post("/books")
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/null-price.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void findAll_Success_ReturnsDtoList() {
        BookDto dto1 = new BookDto()
                .setTitle("CumViatsa")
                .setDescription("testing")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(25.99))
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L))
                .setIsbn("123")
                .setId(1L);
        BookDto dto2 = new BookDto()
                .setTitle("CumViatsa1")
                .setDescription("testing")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(95.99))
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L))
                .setIsbn("124")
                .setId(2L);

        MvcResult result = mockMVC.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = List.of(dto1, dto2);
        List<BookDto> actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0), "id");
    }

    @Test
    @SneakyThrows
    void getBookById_ValidId_ReturnsDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("CumViatsa")
                .setIsbn("123")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(25.99))
                .setDescription("testing")
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L));

        BookDtoWithoutCategoryIds expected = new BookDtoWithoutCategoryIds()
                .setAuthor(requestDto.getAuthor())
                .setTitle(requestDto.getTitle())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice())
                .setDescription(requestDto.getDescription())
                .setCoverImage(requestDto.getCoverImage())
                .setId(1L);

        MvcResult result = mockMVC.perform(get("/books/{id}", expected.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        BookDtoWithoutCategoryIds actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(),
                BookDtoWithoutCategoryIds.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    @Sql(scripts = {
            "classpath:database/books/restore-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_ValidIdAndRequestDto_ReturnsDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("CumViatsaA")
                .setIsbn("123")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(25.99))
                .setDescription("testing")
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L));

        BookDto expected = new BookDto()
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds())
                .setCoverImage(requestDto.getCoverImage())
                .setId(1L);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMVC.perform(put("/books/{id}", 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void updateBook_failedIsbnValidation_test() {
        mockMVC.perform(put("/books/{id}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request/book/empty-isbn.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateBook_failedTitleValidation_test() {
        mockMVC.perform(put("/books/{id}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/invalid-title.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateBook_failedAuthorValidation_test() {
        mockMVC.perform(put("/books/{id}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request/book/empty-author.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateBook_failedCategoriesValidation_test() {
        mockMVC.perform(put("/books/{id}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request"
                                                        + "/book/empty-categories.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateBook_failedPriceValidation_test() {
        mockMVC.perform(put("/books/{id}", 1)
                        .content(new String(
                                Files.readAllBytes(
                                        new File(
                                                "src/test/resources/request/book/null-price.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @Sql(scripts = {
            "classpath:database/books/restore-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidId_Success() {
        mockMVC.perform(delete("/books/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void searchBooks_ValidParameters_ReturnsDtoList() {
        BookDto dto1 = new BookDto()
                .setTitle("CumViatsa")
                .setDescription("testing")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(25.99))
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L))
                .setIsbn("123")
                .setId(1L);
        BookDto dto2 = new BookDto()
                .setTitle("CumViatsa1")
                .setDescription("testing")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(95.99))
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L))
                .setIsbn("124")
                .setId(2L);

        List<BookDto> expected = List.of(dto1, dto2);
        BookSearchParametersDto searchParams = new BookSearchParametersDto(
                new String[]{"Eslava"},
                null,
                null,
                null);
        MvcResult result = mockMVC.perform(get("/books/search")
                        .content(objectMapper.writeValueAsString(searchParams))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
