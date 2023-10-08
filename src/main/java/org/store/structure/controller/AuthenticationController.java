package org.store.structure.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.structure.dto.user.UserLoginRequestDto;
import org.store.structure.dto.user.UserLoginResponseDto;
import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.exception.RegistrationException;
import org.store.structure.security.AuthenticationService;
import org.store.structure.service.user.UserService;

@Tag(name = "User management", description = "Endpoints to manage users")
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto request) {
        log.info("login method started");
        return authenticationService.authenticate(request);
    }

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid
            UserRegistrationRequestDto request) throws RegistrationException {
        log.info("register method start");
        return userService.register(request);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/set/admin/{id}")
    @Transactional
    public String setAdminRole(@PathVariable Long id) {
        return userService.setAdminRole(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/set/user/{id}")
    @Transactional
    public String setUserRole(@PathVariable Long id) {
        return userService.setUserRole(id);
    }
}
