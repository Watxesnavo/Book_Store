package org.store.structure.service.user;

import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    String setAdminRole(Long id);

    String setUserRole(Long id);
}
