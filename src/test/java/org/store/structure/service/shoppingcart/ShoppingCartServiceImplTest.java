package org.store.structure.service.shoppingcart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.store.structure.dto.cartitem.CartItemRequestDto;
import org.store.structure.dto.cartitem.CartItemResponseDto;
import org.store.structure.dto.cartitem.CartItemUpdateDto;
import org.store.structure.dto.shoppingcart.ShoppingCartResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.ShoppingCartMapper;
import org.store.structure.model.Book;
import org.store.structure.model.CartItem;
import org.store.structure.model.Category;
import org.store.structure.model.Role;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.cartitem.CartItemRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    private ShoppingCart shoppingCart;
    private User user;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeEach
    void setUp() {
        user = initUser();
        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of(initCartItem()));
    }


    @Test
    void getCurrentCart_WithValidUser_ReturnsShoppingCartEntity() {
        when(shoppingCartRepository.findFirstByUserEmail(user.getEmail())).thenReturn(Optional.of(shoppingCart));

        ShoppingCart actual = shoppingCartService.getCurrentCart(user);

        assertEquals(shoppingCart, actual);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void getCurrentCart_WithInvalidUser_ThrowsException() {
        when(shoppingCartRepository.findFirstByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.getCurrentCart(user));

        String expected = "Can't find a cart by email: " + user.getUsername();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void updateItemQuantity_WithValidItemIdAndDto_ReturnsResponseDto() {
        CartItemUpdateDto updateDto = initCartItemUpdateDto();
        CartItem cartItem = initCartItem();
        ShoppingCartResponseDto expected = initShoppingCartResponseDto();

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(shoppingCartRepository.findFirstByUserEmail(user.getEmail())).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService.updateItemQuantity(cartItem.getId(), updateDto, user);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper, cartItemRepository);
    }

    @Test
    void updateItemQuantity_WithInvalidItemId_ThrowsException() {
        CartItemUpdateDto updateDto = initCartItemUpdateDto();
        CartItem cartItem = initCartItem();

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.updateItemQuantity(cartItem.getId(), updateDto, user)
        );

        String expected = "Can't find item by id: "
                + cartItem.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void addBook_WithValidRequestDto_ReturnsResponseDto() {
        CartItemRequestDto requestDto = initCartItemRequestDto();
        CartItem cartItem = initCartItem();
        Book book = initBook();
        ShoppingCartResponseDto expected = initShoppingCartResponseDto();

        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findFirstByUserEmail(user.getEmail())).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.save(any())).thenReturn(cartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService.addBook(requestDto, user);
        assertEquals(expected, actual);
        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    void addBook_WithInvalidBookId_ThrowsException() {
        CartItemRequestDto requestDto = initCartItemRequestDto();
        requestDto.setBookId(5L);

        when(bookRepository.findById(requestDto.getBookId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addBook(requestDto, user)
        );

        String expected = "Can't find book by id: " + requestDto.getBookId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(cartItemRepository, shoppingCartRepository);
    }

    private CartItemRequestDto initCartItemRequestDto() {
        CartItemRequestDto requestDto = new CartItemRequestDto();
        CartItem cartItem = initCartItem();
        requestDto.setQuantity(cartItem.getQuantity());
        requestDto.setBookId(cartItem.getBook().getId());
        return requestDto;
    }

    private User initUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("vs@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("Vyacheslav");
        user.setLastName("Sukhov");
        user.setRoles(Set.of(new Role(Role.RoleName.ADMIN)));
        user.setShippingAddress("unknown");
        return user;
    }

    private CartItem initCartItem() {
        CartItem cartItem = new CartItem();
        Book book = initBook();
        book.setCategories(Set.of(initCategory()));
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(1);
        return cartItem;
    }

    private Category initCategory() {
        Category category = new Category();
        category.setName("Biography");
        category.setDescription("Personal stories");
        category.setId(1L);
        return category;
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

    private CartItemUpdateDto initCartItemUpdateDto() {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setQuantity(initCartItem().getQuantity());
        return updateDto;
    }

    private CartItemResponseDto initCartItemResponseDto() {
        Book book = initBook();
        CartItemResponseDto responseDto = new CartItemResponseDto();
        responseDto.setBookTitle(book.getTitle());
        responseDto.setBookId(book.getId());
        responseDto.setShoppingCartId(shoppingCart.getId());
        responseDto.setQuantity(1);
        return responseDto;
    }

    private ShoppingCartResponseDto initShoppingCartResponseDto() {
        ShoppingCartResponseDto responseDto = new ShoppingCartResponseDto();
        responseDto.setId(shoppingCart.getId());
        responseDto.setUserId(user.getId());
        responseDto.setCartItems(Set.of(initCartItemResponseDto()));
        return responseDto;
    }
}