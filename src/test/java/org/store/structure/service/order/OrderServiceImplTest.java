package org.store.structure.service.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.store.structure.dto.order.OrderRequestDto;
import org.store.structure.dto.order.OrderResponseDto;
import org.store.structure.dto.order.OrderStatusUpdateDto;
import org.store.structure.dto.orderitem.OrderItemResponseDto;
import org.store.structure.exception.EntityNotFoundException;
import org.store.structure.mapper.OrderItemMapper;
import org.store.structure.mapper.OrderMapper;
import org.store.structure.model.Book;
import org.store.structure.model.CartItem;
import org.store.structure.model.Order;
import org.store.structure.model.OrderItem;
import org.store.structure.model.Role;
import org.store.structure.model.ShoppingCart;
import org.store.structure.model.User;
import org.store.structure.repository.book.BookRepository;
import org.store.structure.repository.order.OrderRepository;
import org.store.structure.repository.orderitem.OrderItemRepository;
import org.store.structure.repository.shoppingcart.ShoppingCartRepository;
import org.store.structure.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private User user;
    private Order order;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderMapper orderMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        user = initUser();
        order = initOrder();
        order.setOrderItems(Set.of(initOrderItem()));

    }

    @Test
    void placeOrder_WithValidRequestDto_ReturnsResponseDto() {
        OrderItem orderItem = initOrderItem();
        Book book = initBook();
        ShoppingCart shoppingCart = initShoppingCart();
        OrderResponseDto expected = initOrderResponseDto(order);
        OrderRequestDto requestDto = initOrderRequestDto(order);
        order.setId(null);
        orderItem.setOrder(order);
        orderItem.setId(null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderItemRepository.findAllByOrderUserEmail(user.getUsername())).thenReturn(Set.of(orderItem));
        when(orderItemRepository.findAll()).thenReturn(List.of(orderItem));
        when(shoppingCartRepository.findFirstByUserEmail(user.getEmail())).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(orderMapper.toDto(order)).thenReturn(expected);
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponseDto actual = orderService.placeOrder(requestDto, user);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(
                userRepository,
                orderRepository,
                orderMapper,
                orderItemRepository,
                shoppingCartRepository,
                bookRepository
        );
    }

    @Test
    void getOrderHistory_Valid_ReturnsAllOrders() {
        OrderResponseDto responseDto = initOrderResponseDto(order);
        List<OrderResponseDto> expected = List.of(responseDto);

        when(orderMapper.toDto(order)).thenReturn(responseDto);
        when(orderRepository.findAllByUserEmail(user.getEmail())).thenReturn(List.of(order));

        List<OrderResponseDto> actual = orderService.getOrderHistory(user);

        assertEquals(expected, actual);
        verifyNoMoreInteractions(orderRepository, orderMapper);
    }

    @Test
    void updateStatus_WithValidOrderIdAndStatusRequest_ReturnsResponseDto() {
        OrderResponseDto expected = initOrderResponseDto(order);
        OrderStatusUpdateDto requestDto = new OrderStatusUpdateDto();
        requestDto.setStatus("DELIVERED");

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(expected);

        OrderResponseDto actual = orderService.updateStatus(order.getId(), requestDto);
        expected.setStatus(Order.Status.DELIVERED);

        assertEquals(expected.getStatus(), actual.getStatus());
        verifyNoMoreInteractions(orderMapper, orderRepository);
    }

    @Test
    void updateStatus_WithInvalidOrderId_ThrowsException() {
        OrderStatusUpdateDto requestDto = new OrderStatusUpdateDto();

        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.updateStatus(order.getId(), requestDto)
        );

        String expected = "Can't find order by id: " + order.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(orderMapper, orderRepository);
    }

    @Test
    void updateStatus_WithInvalidStatus_ThrowsException() {
        OrderStatusUpdateDto requestDto = new OrderStatusUpdateDto();
        requestDto.setStatus("Nullable");

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateStatus(order.getId(), requestDto)
        );
        String expected = "No enum constant org.store.structure.model.Order.Status."
                + requestDto.getStatus();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(orderMapper, orderRepository);
    }

    @Test
    void getItemsForOrder_WithValidOrderId_ReturnsAllItems() {
        OrderItem orderItem = initOrderItem();
        OrderItemResponseDto responseDto = initOrderItemResponseDto(orderItem);
        Set<OrderItem> expected = Set.of(orderItem);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderItemMapper.toDto(orderItem)).thenReturn(responseDto);

        Set<OrderItemResponseDto> actual = orderService.getItemsForOrder(order.getId());

        assertEquals(expected.size(), actual.size());
        verifyNoMoreInteractions(orderRepository, orderItemMapper);
    }

    @Test
    void getItemsForOrder_WithInvalidOrderId_ThrowsException() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getItemsForOrder(order.getId())
        );

        String expected = "Can't find order by id: " + order.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getSpecificItemFromSpecificOrder_WithValidOrderIdAndItemId_ReturnsResponseDto() {
        OrderItem orderItem = initOrderItem();
        OrderItemResponseDto responseDto = initOrderItemResponseDto(orderItem);

        when(orderItemRepository.findByOrderIdAndId(order.getId(), orderItem.getId()))
                .thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(orderItem)).thenReturn(responseDto);

        OrderItemResponseDto actual = orderService.getSpecificItemFromSpecificOrder(order.getId(), orderItem.getId());

        assertEquals(responseDto, actual);
        verifyNoMoreInteractions(orderItemRepository, orderItemMapper);
    }

    @Test
    void getSpecificItemFromSpecificOrder_WithInvalidOrderIdOrItemId_ThrowsException() {
        OrderItem orderItem = initOrderItem();

        when(orderItemRepository.findByOrderIdAndId(anyLong(),
                anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getSpecificItemFromSpecificOrder(order.getId(), orderItem.getId()));

        String expected = "Can't find item by id: " + orderItem.getId() + " or order id: " + order.getId();
        assertEquals(expected, exception.getMessage());
        verifyNoMoreInteractions(orderItemRepository);
    }

    private Order initOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(Order.Status.CREATED);
        order.setUser(user);
        order.setShippingAddress("unknown");
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(BigDecimal.ZERO);
        return order;
    }

    private OrderItem initOrderItem() {
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(9.99));
        item.setQuantity(1);
        item.setBook(initBook());
        return item;
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

    private OrderResponseDto initOrderResponseDto(Order order) {
        OrderItem orderItem = initOrderItem();
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setId(order.getId());
        responseDto.setOrderItems(Set.of(initOrderItemResponseDto(orderItem)));
        responseDto.setOrderDate(order.getOrderDate());
        responseDto.setStatus(order.getStatus());
        responseDto.setTotal(order.getTotal());
        return responseDto;
    }

    private OrderItemResponseDto initOrderItemResponseDto(OrderItem item) {
        OrderItemResponseDto responseDto = new OrderItemResponseDto();
        responseDto.setId(item.getId());
        responseDto.setQuantity(item.getQuantity());
        responseDto.setBookId(item.getBook().getId());
        return responseDto;
    }

    private OrderRequestDto initOrderRequestDto(Order order) {
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setShippingAddress(order.getShippingAddress());
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
}