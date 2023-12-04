package org.store.structure.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.store.structure.dto.book.BookDto;
import org.store.structure.dto.book.BookDtoWithoutCategoryIds;
import org.store.structure.dto.book.BookSearchParametersDto;
import org.store.structure.dto.book.CreateBookRequestDto;
import org.store.structure.dto.category.CategoryRequestDto;
import org.store.structure.dto.category.CategoryResponseDto;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username = "admin", authorities = {"ADMIN"})
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
                    new ClassPathResource("database/categories/add-category-to-categories-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-book-to-books-table.sql")
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
                    new ClassPathResource("database/bookscategories/delete-categories-from-books.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/categories/delete-category.sql"));
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/delete-book.sql"));
        }
    }

    @Test
    @SneakyThrows
    void createBook_ValidRequestDto_ReturnsResponse() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Test")
                .setIsbn("1234")
                .setAuthor("Sukhov")
                .setPrice(BigDecimal.TEN)
                .setDescription("testing")
                .setCoverImage("coverTest")
                .setCategoryIds(Set.of(1L));

        CategoryRequestDto category = new CategoryRequestDto()
                .setName("test")
                .setDescription("testing");
        MvcResult categoryResult = mockMVC.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto expected = new BookDto()
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds())
                .setCoverImage(requestDto.getCoverImage())
                .setId(objectMapper.readValue(
                        categoryResult.getResponse().getContentAsString(),
                        CategoryResponseDto.class).getId()
                );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMVC.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void findAll_Success_ReturnsDtoList() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("Test")
                .setIsbn("1234")
                .setAuthor("Sukhov")
                .setPrice(BigDecimal.TEN)
                .setDescription("testing")
                .setCoverImage("coverTest")
                .setCategoryIds(Set.of(1L));

        BookDto bookDto = new BookDto()
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds())
                .setCoverImage(requestDto.getCoverImage())
                .setId(1L);

        MvcResult result = mockMVC.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> expected = List.of(bookDto);
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
    void updateBook_ValidId_ReturnsDto() {
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
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    @Sql(scripts = {
            "classpath:database/books/restore-book.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_ValidId_Success() {
        mockMVC.perform(delete("/books/{id}", 1)).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void searchBooks_ValidParameters_ReturnsDtoList() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setTitle("CumViatsa")
                .setIsbn("123")
                .setAuthor("Eslava")
                .setPrice(BigDecimal.valueOf(25.99))
                .setDescription("testing")
                .setCoverImage("coverImage")
                .setCategoryIds(Set.of(1L));

        BookDto resultDto = new BookDto()
                .setAuthor(requestDto.getAuthor())
                .setIsbn(requestDto.getIsbn())
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setPrice(requestDto.getPrice())
                .setCategoryIds(requestDto.getCategoryIds())
                .setCoverImage(requestDto.getCoverImage())
                .setId(1L);

        List<BookDto> expected = List.of(resultDto);
        BookSearchParametersDto searchParams = new BookSearchParametersDto(
                new String[]{"Sukhov"},
                null,
                null,
                null);
        String jsonSearchRequest = objectMapper.writeValueAsString(searchParams);
        MvcResult result = mockMVC.perform(get("/books/search")
                        .content(jsonSearchRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0).getTitle(), actual.get(0).getTitle());
    }
}