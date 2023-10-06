package org.store.structure.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.exception.RegistrationException;
import org.store.structure.mapper.UserMapper;
import org.store.structure.model.Role;
import org.store.structure.model.User;
import org.store.structure.repository.user.UserRepository;
import org.store.structure.service.role.RoleService;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        Role userRole = new Role();
        userRole.setRoleName(Role.RoleName.USER);
        List<Role> roles = roleService.findAll();
        for (Role role : roles) {
            if (role.getRoleName().equals(Role.RoleName.USER)) {
                user.setRoles(Set.of(role));
            } else {
                roleService.save(userRole);
                user.setRoles(Set.of(userRole));
                break;
            }
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
