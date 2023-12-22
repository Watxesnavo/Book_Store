package org.store.structure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.store.structure.dto.user.UserLoginRequestDto;
import org.store.structure.dto.user.UserLoginResponseDto;
import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;

import javax.sql.DataSource;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    protected static MockMvc mockMVC;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SneakyThrows
    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
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
    @WithUserDetails(value = "vs@gmail.com", userDetailsServiceBeanName = "customUserDetailsService")
    void login_ValidRequestDto_ReturnsResponseDto() {
        UserLoginRequestDto requestDto = new UserLoginRequestDto("vs@gmail.com", "12345678");
        MvcResult result = mockMVC.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserLoginResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserLoginResponseDto.class);
        assertNotNull(actual);
    }

    @Test
    @SneakyThrows
    void login_failedEmailValidation_test() {
        mockMVC.perform(post("/auth/login")
                        .content(new String(
                                Files.readAllBytes(
                                        new File("src/test/resources/request/user/"
                                                + "user-login-request_invalid-email.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void login_failedPasswordValidation_test() {
        mockMVC.perform(post("/auth/login")
                        .content(new String(
                                Files.readAllBytes(
                                        new File("src/test/resources/request/user"
                                                + "/user-login-request_invalid-password.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void register_ValidRequestDto_ReturnsResponseDto() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto()
                .setEmail("vs@gmail.com")
                .setPassword("12345678")
                .setRepeatPassword("12345678")
                .setFirstName("slava")
                .setLastName("sukhov")
                .setShippingAddress("sagunt 39");

        MvcResult result = mockMVC.perform(post("/auth/register")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
    }

    @Test
    @SneakyThrows
    void register_failedPasswordValidation_test() {
        mockMVC.perform(post("/auth/register")
                        .content(new String(
                                Files.readAllBytes(
                                        new File("src/test/resources/request/user"
                                                + "/user-register-request_empty-password.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void register_failedEmailValidation_test() {
        mockMVC.perform(post("/auth/register")
                        .content(new String(
                                Files.readAllBytes(
                                        new File("src/test/resources/request/user"
                                                + "/user-register-request_invalid-email.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void register_failedNameValidation_test() {
        mockMVC.perform(post("/auth/register")
                        .content(new String(
                                Files.readAllBytes(
                                        new File("src/test/resources/request/user"
                                                + "/user-register-request_invalid-name-sizes.json")
                                                .toPath()))
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}