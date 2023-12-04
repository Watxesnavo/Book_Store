package org.store.structure.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.store.structure.dto.user.UserRegistrationRequestDto;
import org.store.structure.dto.user.UserResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.exception.RegistrationException;
import org.store.structure.mapper.UserMapper;
import org.store.structure.model.Book;
import org.store.structure.model.CartItem;
import org.store.structure.model.Role;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;
import org.store.structure.repository.role.RoleRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;
import org.store.structure.repository.user.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private PasswordEncoder passwordEncoder;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = initUser();
    }

    @Test
    void register_WithValidRequestDto_ReturnsResponseDto() {
        UserRegistrationRequestDto userRegistrationRequestDto = initRegistrationDto();
        UserResponseDto expected = initResponseDto();
        Role role = initRole();
        ShoppingCart shoppingCart = initShoppingCart();
        user.setId(null);
        user.setPassword(null);

        when(userRepository.findByEmail(userRegistrationRequestDto.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(any())).thenReturn(role);
        when(shoppingCartRepository.save(any())).thenReturn(shoppingCart);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(expected);

        UserResponseDto actual = userService.register(userRegistrationRequestDto);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        UserRegistrationRequestDto userRegistrationRequestDto = initRegistrationDto();
        user.setId(null);
        user.setPassword(null);

        when(userRepository.findByEmail(userRegistrationRequestDto.getEmail())).thenReturn(Optional.of(user));

        RegistrationException exception = assertThrows(
                RegistrationException.class,
                () -> userService.register(userRegistrationRequestDto)
        );

        String expected = "This user is already registered";
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void setRoleToUser_WithValidUserId_ReturnsConfirmationString() {
        String userRoleName = "USER";
        Role userRole = initRole();
        userRole.setRoleName(Role.RoleName.valueOf(userRoleName));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(Role.RoleName.valueOf(userRoleName))).thenReturn(userRole);

        String actualUser = userService.setRoleToUser(user.getId(), userRoleName);

        String expectedUser = "User by id: " + user.getId() + " has role: USER";
        assertEquals(expectedUser, actualUser);

        String adminRoleName = "ADMIN";
        Role adminRole = initRole();
        adminRole.setRoleName(Role.RoleName.valueOf(adminRoleName));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(Role.RoleName.valueOf(adminRoleName))).thenReturn(adminRole);

        String actualAdmin = userService.setRoleToUser(user.getId(), adminRoleName);

        String expectedAdmin = "User by id: " + user.getId() + " has role: ADMIN";
        assertEquals(expectedAdmin, actualAdmin);
        verifyNoMoreInteractions(userRepository, roleRepository);
    }

    @Test
    void setRoleToUser_WithInvalidUserId_ThrowsException() {
        String userRoleName = "USER";

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.setRoleToUser(user.getId(), userRoleName)
        );

        String expected = "Can't find user by userId: " + user.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(userRepository, roleRepository);
    }

    private User initUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("vs@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Vyacheslav");
        user.setLastName("Sukhov");
        user.setShippingAddress("unknown");
        return user;
    }

    private UserRegistrationRequestDto initRegistrationDto() {
        UserRegistrationRequestDto requestRegisterDto = new UserRegistrationRequestDto();
        requestRegisterDto.setEmail(user.getEmail());
        requestRegisterDto.setPassword(user.getPassword());
        requestRegisterDto.setFirstName(user.getFirstName());
        requestRegisterDto.setShippingAddress(user.getShippingAddress());
        requestRegisterDto.setRepeatPassword(user.getPassword());
        requestRegisterDto.setLastName(user.getLastName());
        return requestRegisterDto;
    }

    private UserResponseDto initResponseDto() {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setEmail(user.getEmail());
        responseDto.setId(user.getId());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        responseDto.setShippingAddress(user.getShippingAddress());
        return responseDto;
    }

    private Role initRole() {
        return new Role();
    }

    private ShoppingCart initShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        CartItem cartItem = initCartItem();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(cartItem));
        return shoppingCart;
    }

    private CartItem initCartItem() {
        Book book = initBook();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(1);
        cartItem.setBook(book);
        return cartItem;
    }

    private Book initBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Vyacheslav");
        book.setDescription("stories of vyacheslav");
        book.setTitle("My Story");
        book.setPrice(BigDecimal.valueOf(9.99));
        book.setIsbn("12345");
        book.setCoverImage("Test.jpg");
        return book;
    }
}