package org.store.structure.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.exception.RegistrationException;
import org.store.structure.mapper.UserMapper;
import org.store.structure.model.User;
import org.store.structure.repository.user.UserRepository;
import org.store.structure.service.role.RoleService;

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
            throw new RegistrationException("This user is already registered");
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        user.setRoles(Set.of(roleService.findByRoleName("USER")));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public String setAdminRole(Long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        user.setRoles(Set.of(roleService.findByRoleName("ADMIN")));
        return "this user has admin authorities now";
    }

    @Override
    public String setUserRole(Long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        user.setRoles(Set.of(roleService.findByRoleName("USER")));
        return "this user has user authorities now";
    }
}
