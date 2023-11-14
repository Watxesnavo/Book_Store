package org.store.structure.service.user;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.exception.RegistrationException;
import org.store.structure.mapper.UserMapper;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;
import org.store.structure.repository.user.UserRepository;
import org.store.structure.service.role.RoleService;

@RequiredArgsConstructor
@Component
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final ShoppingCartRepository cartRepository;

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
        User saved = userRepository.save(user);
        setShoppingCartToUser(saved);
        return userMapper.toUserResponse(saved);
    }

    @Override
    public String setAdminRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id: " + id));
        user.setRoles(Set.of(roleService.findByRoleName("ADMIN")));
        return "this user has admin authorities now";
    }

    @Override
    public String setUserRole(Long id) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        user.setRoles(Set.of(roleService.findByRoleName("USER")));
        return "this user has user authorities now";
    }

    private void setShoppingCartToUser(User user) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUser(user);
        cartRepository.save(cart);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find a user by email"));
    }
}
